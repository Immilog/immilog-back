package com.backend.immilog.interaction.presentation.controller;

import com.backend.immilog.interaction.application.services.CreateInteractionUseCase;
import com.backend.immilog.interaction.application.services.InteractionDeleteService;
import com.backend.immilog.interaction.presentation.payload.InteractionCreateRequest;
import com.backend.immilog.interaction.presentation.payload.InteractionResponse;
import com.backend.immilog.shared.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {
    private final CreateInteractionUseCase createInteractionUseCase;
    private final InteractionDeleteService interactionDeleteService;

    @PostMapping
    public ResponseEntity<InteractionResponse> createInteraction(
            @CurrentUser String userId,
            @RequestBody InteractionCreateRequest request
    ) {
        var command = request.toCommand(userId);
        var result = createInteractionUseCase.toggleInteraction(command);
        return ResponseEntity.ok(InteractionResponse.success(result.toInfraDTO()));
    }

    @DeleteMapping("/{interactionId}")
    public ResponseEntity<InteractionResponse> deleteInteraction(@PathVariable("interactionId") String interactionId) {
        interactionDeleteService.delete(interactionId);
        return ResponseEntity.ok(InteractionResponse.success("Interaction deleted successfully"));
    }
}