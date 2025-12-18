package com.example.spring_ai_demo.adapter.in.web;

import com.example.petstore.server.api.PetApi;
import com.example.petstore.server.model.ModelApiResponse;
import com.example.petstore.server.model.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pet-store")
public class PetStorePetController implements PetApi {

    @Override
    public ResponseEntity<Pet> addPet(Pet pet) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deletePet(Long petId, Optional<String> apiKey) {
        return null;
    }

    @Override
    public ResponseEntity<List<Pet>> findPetsByStatus(List<String> status) {
        return null;
    }

    @Override
    public ResponseEntity<List<Pet>> findPetsByTags(List<String> tags) {
        return null;
    }

    @Override
    public ResponseEntity<Pet> getPetById(Long petId) {
        return null;
    }

    @Override
    public ResponseEntity<Pet> updatePet(Pet pet) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updatePetWithForm(Long petId, Optional<String> name, Optional<String> status) {
        return null;
    }

    @Override
    public ResponseEntity<ModelApiResponse> uploadFile(Long petId, Optional<String> additionalMetadata, MultipartFile file) {
        return null;
    }
}
