package com.backend.immilog.interaction.presentation.controller;

import com.backend.immilog.interaction.application.services.InteractionUserCommandService;
import com.backend.immilog.interaction.application.usecase.InteractionCreateUseCase;
import com.backend.immilog.interaction.presentation.payload.InteractionCreateRequest;
import com.backend.immilog.interaction.presentation.payload.InteractionResponse;
import com.backend.immilog.shared.annotation.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {
    private final InteractionCreateUseCase interactionCreateUseCase;
    private final InteractionUserCommandService interactionUserCommandService;

    public InteractionController(
            InteractionCreateUseCase interactionCreateUseCase,
            InteractionUserCommandService interactionUserCommandService
    ) {
        this.interactionCreateUseCase = interactionCreateUseCase;
        this.interactionUserCommandService = interactionUserCommandService;
    }

    @PostMapping
    public ResponseEntity<InteractionResponse> createInteraction(
            @CurrentUser String userId,
            @RequestBody InteractionCreateRequest request
    ) {
        var command = request.toCommand(userId);
        var result = interactionCreateUseCase.createInteraction(command);
        return ResponseEntity.ok(InteractionResponse.success(result));
    }

    @DeleteMapping("/{interactionId}")
    public ResponseEntity<InteractionResponse> deleteInteraction(@PathVariable String interactionId) {
        interactionUserCommandService.deleteInteraction(interactionId);
        return ResponseEntity.ok(InteractionResponse.success("Interaction deleted successfully"));
    }
}