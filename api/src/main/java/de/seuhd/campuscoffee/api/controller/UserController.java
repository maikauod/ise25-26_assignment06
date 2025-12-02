package de.seuhd.campuscoffee.api.controller;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import de.seuhd.campuscoffee.api.mapper.UserDtoMapper;
import de.seuhd.campuscoffee.domain.ports.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static de.seuhd.campuscoffee.api.util.ControllerUtils.getLocation;

@Tag(name = "Users", description = "Operations related to user management.")
@Controller
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    //DONE: Implement user controller
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    //upsert method to be used later (similar to implementation with pos)
    private UserDto upsert(UserDto userDto) {
        return userDtoMapper.fromDomain(
                userService.upsert(
                        userDtoMapper.toDomain(userDto)
                )
        );
    }


    //retrieve users
    @Operation(
            summary = "Retrieve all users",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDto.class)
                            ),
                            description = "All users given as a json array"
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers().stream()
                        .map(userDtoMapper::fromDomain)
                        .toList()
        );
    }


    //retrieve users by id
    @Operation(
            summary = "Retrieve a specific user by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "the corresponding user is given as a json object"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "no users found with this ID"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserByID(
            @PathVariable Long id){

        return ResponseEntity.ok(
                userDtoMapper.fromDomain(userService.getUserByID(id))
        );

    }


    //retrieve users by username bzw login name via filter endpoint
    @Operation(
            summary= "retrieve user by username/login name",
            responses= {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "the corresponding user is given as a json object"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "no users found with the given username"
                    )
            }


    )
    @GetMapping("/filter")
    public ResponseEntity<UserDto> filter(
            @RequestParam("username") String username){


        return ResponseEntity.ok(
                userDtoMapper.fromDomain(userService.getByUsername(username))
        );
    }

    //create new users
    @Operation(
            summary = "create new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "new user created as a json object"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "new user cannot be created due to validation error"
                    )
            }
    )
    @PostMapping("")
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Valid UserDto userDto
    ){
        UserDto created = upsert(userDto);
        return ResponseEntity.created(getLocation(created.id())).body(created);
    }

    //update existing user
    @Operation(
            summary = "update existing user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "the user was updated and saved as a json object"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "validation error: user cannot be updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "no user found"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto userDto
    ){
        if(!id.equals(userDto.id())) {
            throw new IllegalArgumentException("id in path and body dont match");
        }
            return ResponseEntity.ok(
                    upsert(userDto)
            );
    }

    //delete existing user by id
    @Operation(
            summary= "delete user via id",
            responses={
                    @ApiResponse(
                            responseCode = "204",
                            description = "user successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "409", //alternativaly could be 400 or anything in 4xx
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "user could not be deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "no user with this id found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
