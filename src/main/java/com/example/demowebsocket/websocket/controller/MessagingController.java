package com.example.demowebsocket.websocket.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.stereotype.Controller;

import javax.xml.validation.Validator;
import java.lang.reflect.Array;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;

@Controller
public class MessagingController {
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/one")
    @SendTo("/back/a")
    public Object one(String message){
        System.out.println("========== one ==========");
        System.out.println(message);
        System.out.println(JSON.toJSONString(message));
        return "hello - this is one send to a";
    }
    @MessageMapping("/two/{type}")
    public void tow(String message,
                    MessageHeaders headers,
                    Message ms,
                    @Payload String payload,
                    @DestinationVariable String type,
                    @Headers Map hd


    ){



        String key = ((List)((Map) headers.get("nativeHeaders")).get("key")).get(0).toString();

        System.out.println("key : " +key);
        simpMessageSendingOperations.convertAndSendToUser(key,"/self","你好"+key);
    }
}