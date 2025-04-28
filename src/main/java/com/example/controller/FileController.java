package com.example.controller;

import com.example.utils.IdFactoryUtil;
import com.example.utils.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理接口
 * 功能包含：
 * 1. 文件/视频上传
 * 2. 获取文件资源
 */
@RestController
@RequestMapping("/file")
public class FileController {

    // 接口基础路径
    @Value("${my-server.api-context-path}")
    private String API;

    /**
     * 文件上传方法
     *
     * @param multipartFile 上传的文件流对象，包含待保存的文件内容
     * @param fileName      目标保存的文件名称（需包含文件扩展名）
     * @return 上传操作结果：true 表示文件保存成功，false 表示目录创建失败/文件删除失败/文件创建失败
     * @throws IOException 当发生文件I/O操作异常时抛出
     */
    public static boolean fileName(MultipartFile multipartFile, String fileName) throws IOException {
        // 初始化目标目录（基于类加载路径构建）
        File fileDir = new File(PathUtils.getClassLoadRootPath() + "/assets/picture");

        // 检查并创建不存在的目标目录
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return false;
            }
        }
        // 构建完整文件路径
        File file = new File(fileDir.getAbsolutePath() + "/" + fileName);

        // 处理已存在的同名文件
        if (file.exists()) {
            if (!file.delete()) {
                return false;
            }
        }
        // 创建新文件并写入内容
        if (file.createNewFile()) {
            multipartFile.transferTo(file);
            return true;
        }
        return false;
    }

    /**
     * 文件上传接口
     *
     * @param multipartFile 客户端上传的文件流，参数名称需与前端form-data字段名"file"保持一致
     * @return Map<String, Object> 包含操作结果的响应对象：
     * - code: 状态码（200成功，400失败）
     * - data: 成功时返回文件访问地址（包含API前缀和唯一文件名）
     * - msg: 失败时的错误信息
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        // 生成唯一文件标识：时间戳+UUID混合算法
        String uuid = IdFactoryUtil.getFileId();
        // 构造带唯一前缀的文件名（原始文件名保留扩展名信息）
        String fileName = uuid + multipartFile.getOriginalFilename();
        Map<String, Object> rep = new HashMap<>();

        try {
            // 调用核心文件上传逻辑，包含文件存储和异常处理
            if (uploadFile(multipartFile, fileName)) {
                rep.put("code", 200);
                // 拼接完整的文件访问URL（API常量需在类中定义）
                rep.put("data", API + "/file/getFile?fileName=" + fileName);
                return rep;
            }
        } catch (IOException e) {
            // 文件IO异常捕获（如存储空间不足、文件写入失败等）
            rep.put("code", 400);
            rep.put("msg", "文件上传异常");
            return rep;
        }
        // 处理非异常情况的失败（如文件大小限制、格式校验不通过等）
        rep.put("code", 400);
        rep.put("msg", "文件上传异常");
        return rep;
    }

    /**
     * 处理视频文件上传请求，生成唯一文件名并保存文件
     *
     * @param multipartFile 上传的视频文件流，需要包含有效的文件内容
     * @return 包含操作结果的Map对象：
     * - code: 200表示成功，400表示失败
     * - data: 成功时返回文件访问URL（格式：API常量值 + "/file/getFile?fileName=" + 生成的文件名）
     * - msg: 失败时返回错误描述信息
     */
    @PostMapping("/video/upload")
    public Map<String, Object> videoUpload(@RequestParam("file") MultipartFile multipartFile) {
        // 生成全局唯一文件名：UUID + 原始文件名
        String uuid = IdFactoryUtil.getFileId();
        String fileName = uuid + multipartFile.getOriginalFilename();
        Map<String, Object> rep = new HashMap<>();

        try {
            // 核心文件上传逻辑，成功返回访问地址
            if (uploadFile(multipartFile, fileName)) {
                rep.put("code", 200);
                rep.put("data", API + "/file/getFile?fileName=" + fileName);
                return rep;
            }
        } catch (IOException e) {
            // 处理文件IO异常情况
            rep.put("code", 400);
            rep.put("msg", "文件上传异常");
            return rep;
        }

        // 处理非异常情况的上传失败
        rep.put("code", 400);
        rep.put("msg", "文件上传异常");
        return rep;
    }

    /**
     * 上传文件到指定存储路径
     * 通过Spring框架的MultipartFile对象处理文件上传，支持自定义存储文件名
     *
     * @param multipartFile Spring框架封装的上传文件流对象，包含文件内容和元数据。不可为null
     * @param fileName      自定义的目标文件名（可包含相对路径），支持扩展名定义。
     *                      示例："images/20240427/avatar.png"
     * @return boolean 上传结果标识，true表示文件写入成功，false表示写入失败
     * @throws IOException 当发生磁盘IO异常时抛出，包括：目录创建失败、文件写入失败、
     *                     磁盘空间不足等情况
     */
    public boolean uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
        return fileName(multipartFile, fileName);
    }

    /**
     * 从指定资源目录获取图片文件并写入HTTP响应流
     *
     * @param imageName 要获取的图片文件名（位于assets/picture目录下）
     * @param response  用于输出图片二进制流的HTTP响应对象
     * @throws IOException 当文件不存在或流操作失败时抛出
     * @apiNote 该方法通过response的输出流直接返回二进制图片数据，无返回值。
     * 文件路径基于类加载根目录构建，要求图片必须存放在assets/picture目录中。
     */
    @GetMapping("/getFile")
    public void getImage(@RequestParam("fileName") String imageName,
                         HttpServletResponse response) throws IOException {
        // 构建目标目录的绝对路径（基于类加载根目录）
        File fileDir = new File(PathUtils.getClassLoadRootPath() + "/assets/picture");

        // 组合得到完整的图片文件路径
        File image = new File(fileDir.getAbsolutePath() + "/" + imageName);

        // 处理文件存在的情况
        if (image.exists()) {
            // 读取文件内容到字节数组
            FileInputStream fileInputStream = new FileInputStream(image);
            byte[] bytes = new byte[fileInputStream.available()];

            // 当成功读取到文件内容时
            if (fileInputStream.read(bytes) > 0) {
                // 将字节数组写入响应输出流
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes);
                outputStream.close();
            }
            fileInputStream.close();
        }
    }
}

