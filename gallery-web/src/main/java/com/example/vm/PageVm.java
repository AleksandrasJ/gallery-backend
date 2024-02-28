package com.example.vm;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@VariableResolver(DelegatingVariableResolver.class)
public class PageVm {

    @Init
    public void init() {

        System.out.println("It WORKS!");

    }
}
