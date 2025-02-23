package com.example.service;

import com.example.pojo.api.Result;
import com.example.pojo.vo.ChartVO;

import java.util.List;

public interface ViewsService {

    Result<List<ChartVO>> staticControls();

}
