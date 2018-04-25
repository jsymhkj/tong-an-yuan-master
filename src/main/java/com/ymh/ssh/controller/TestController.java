package com.ymh.ssh.controller;

import com.ymh.ssh.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by
 * On 11/16/2017.11:59 PM
 */
@Controller
public class TestController {

    @Autowired(required=true)
    private PersonService personService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "test";
    }

    @RequestMapping(value = "/savePerson", method = RequestMethod.GET)
    @ResponseBody
    public String savePerson() {
        personService.savePerson();
        return "success!";
    }

    @RequestMapping(value = "/getPerson", method = RequestMethod.GET)
    @ResponseBody
    public String getPerson() {
        return personService.getPerson(37L);
    }
}
