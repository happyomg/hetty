package io.hetty;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wuxw on 2015/12/2.
 */
@RestController
public class HelloController {

    @RequestMapping("/")
    public String hello() {
        return "Hello World";
    }
}
