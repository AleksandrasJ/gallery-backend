package com.example.vm;

import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

@Getter
@Setter
@VariableResolver(DelegatingVariableResolver.class)
public class MainVm {

    private boolean showUploadButton = true;

    private String keyword;

    @Init
    public void init() {
        keyword = "";
        String currentPage = Executions.getCurrent().getDesktop().getRequestPath();

        System.out.println(currentPage);

        if (currentPage != null && currentPage.endsWith("upload.zul")) {
            showUploadButton = false;
        }
    }

    @Command
    public void doSearchByKeyword() {
        Executions.sendRedirect("/explore.zul?keyword=" + keyword);
    }

    @Command
    public void doRedirectToUpload() {
        Executions.sendRedirect("/upload.zul");
    }
}
