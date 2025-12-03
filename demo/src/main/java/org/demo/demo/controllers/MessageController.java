package org.demo.demo.controllers;

import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.demo.demo.models.AddRole;
import org.demo.demo.models.Message;
import org.demo.demo.services.MessageService;
import org.json.JSONException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/public")
    public Message getPublic() {
        return messageService.getPublicMessage();
    }

    @GetMapping("/protected")
    public Message getProtected() {
        return messageService.getProtectedMessage();
    }

    @GetMapping("/protected/customer")
    @PreAuthorize("hasAuthority('read:customer')")
    public Message getProtectedCustomer(@AuthenticationPrincipal Jwt user) {
        return messageService.getCustomerMessage(user);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public Message getAdmin() {
        return messageService.getAdminMessage();
    }

    @PostMapping("/role")
    @PreAuthorize("hasAuthority('write:role')")
    public Message postRole(@RequestBody AddRole addRole) throws UnirestException, JSONException {
        return messageService.addRole(addRole);
    }
}
