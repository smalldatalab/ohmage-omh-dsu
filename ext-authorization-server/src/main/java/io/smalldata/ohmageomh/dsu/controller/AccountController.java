/*
 * Copyright 2014 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smalldata.ohmageomh.dsu.controller;


import io.smalldata.ohmageomh.dsu.domain.Study;
import io.smalldata.ohmageomh.dsu.service.StudyService;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.repository.EndUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


/**
 * A controller that manages user accounts.
 *
 * @author Cheng-Kang Hsieh
 */
@Controller
public class AccountController {
    @Autowired
    EndUserRepository endUserRepo;
    @Autowired
    StudyService studyService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Authentication auth, Model model) {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName());
        model.addAttribute("user", user.get());
        List<Study> studies = studyService.getStudiesByUsername(user.get().getUsername());
        model.addAttribute("studies", studies);
        return "home";
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String signIn() throws ServletException {
        return "signin";
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    @ResponseBody
    public String signOut(HttpServletRequest request) throws ServletException {
        request.logout();
        return "You have signed out.";
    }
}
