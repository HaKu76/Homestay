package com.example.pojo.vo;

import com.example.pojo.entity.Vendor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VendorVO extends Vendor {
    /**
     * 用户名
     */
    private String userName;
}
