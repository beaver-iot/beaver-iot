package com.milesight.iab.entity.controller;

import com.milesight.iab.entity.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author loong
 * @date 2024/10/16 14:21
 */
@RestController
@RequestMapping("/entity")
public class EntityController {

    @Autowired
    EntityService entityService;


}
