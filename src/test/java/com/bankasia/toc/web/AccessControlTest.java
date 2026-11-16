package com.bankasia.toc.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:secTest")
@AutoConfigureMockMvc
class AccessControlTest {

    @Autowired MockMvc mvc;

    @Test
    void publicPagesNeedNoLogin() throws Exception {
        for (String url : new String[]{"/roster?month=2026-11", "/employees", "/am/roster?month=2026-11",
                "/am/team", "/am/branches", "/am/branches?q=gulshan", "/am/departments"})
            mvc.perform(get(url)).andExpect(status().isOk());
    }

    @Test
    void editPagesRedirectAnonymousToLogin() throws Exception {
        for (String url : new String[]{"/roster/2026-11-02/edit", "/am/roster/2026-11-02/edit",
                "/am/team/new", "/am/branches/groups/1/edit", "/employees/new"})
            mvc.perform(get(url)).andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void anonymousCannotModify() throws Exception {
        mvc.perform(post("/am/departments").with(csrf()).param("name", "X").param("memberId", "1"))
           .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenEditPages() throws Exception {
        mvc.perform(get("/am/roster/2026-11-02/edit")).andExpect(status().isOk());
        mvc.perform(get("/roster/2026-11-02/edit")).andExpect(status().isOk());
        mvc.perform(get("/am/branches/groups/1/edit")).andExpect(status().isOk());
    }
}
