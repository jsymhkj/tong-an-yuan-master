package com.ymh.ssh.service.impl;

import com.alibaba.fastjson.JSON;
import com.ymh.ssh.entity.Person;
import com.ymh.ssh.repository.PersonRepository;
import com.ymh.ssh.service.PersonService;
import com.ymh.ssh.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * Created by
 * On 11/16/2017.11:58 PM
 */
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired(required = true)
    private PersonRepository personRepository;

    @Resource
    private RedisCacheUtil redisCache;

    private static String tableName ="person";

    @Override
    public Long savePerson() {
        Person person = new Person();
        person.setUsername("XRog");
        person.setPhone("18381005946");
        person.setAddress("chenDu");
        person.setRemark("this is XRog");
        return personRepository.save(person);
    }

    @Override
    public String getPerson(Long id){
        String jsonString = redisCache.hget(tableName,id.toString());
        Person person = JSON.parseObject(jsonString, Person.class);
        return person.getUsername();
    }
}
