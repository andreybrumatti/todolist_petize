package com.testpetize.todolist.service;

import com.testpetize.todolist.domain.enuns.TaskPriority;
import com.testpetize.todolist.domain.enuns.TaskStatus;
import com.testpetize.todolist.domain.model.Task;
import com.testpetize.todolist.domain.model.Users;
import com.testpetize.todolist.dto.task.TaskRequestDTO;
import com.testpetize.todolist.dto.task.TaskResponseDTO;
import com.testpetize.todolist.dto.user.LoginRequestDTO;
import com.testpetize.todolist.dto.user.LoginResponseDTO;
import com.testpetize.todolist.exception.TaskException;
import com.testpetize.todolist.exception.UsersException;
import com.testpetize.todolist.repository.TaskRepository;
import com.testpetize.todolist.repository.UsersRepository;
import com.testpetize.todolist.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.core.ParameterizedTypeReference;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private TaskService taskService;


    @Nested
    class createTask {
        @Test
        @DisplayName("Should create task parent with success")
        void shouldCreateTaskParent() {
            //Arrange
            Users user = new Users();
            user.setId(UUID.randomUUID());
            user.setName("Andrey");
            user.setEmail("andrey@teste.com");
            user.setPassword("admin123");

            TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                    "Title Test",
                    "Description test",
                    ZonedDateTime.now().plusDays(3),
                    TaskStatus.PENDING,
                    TaskPriority.HIGH,
                    null
            );

            when(authentication.getName()).thenReturn("andrey@teste.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(taskRepository.save(any(Task.class))).thenAnswer(
                    invocationOnSave -> invocationOnSave.getArgument(0)
            );

            //Act
            TaskResponseDTO taskResponseDTO = taskService.createTask(taskRequestDTO);

            //Assert
            verify(userRepository, times(1)).findByEmail(user.getEmail());
            verify(taskRepository, times(0)).findById(any(UUID.class));
            verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
            var taskCaptureValue = taskArgumentCaptor.getValue();

            assertNotNull(taskCaptureValue);
            assertNotNull(taskResponseDTO);

            assertNull(taskResponseDTO.parentTaskId());

            assertEquals(user, taskCaptureValue.getUser());

            assertEquals(taskCaptureValue.getTitle(), taskResponseDTO.title());
            assertEquals(taskCaptureValue.getDescription(), taskResponseDTO.description());
            assertEquals(taskCaptureValue.getStatus(), taskResponseDTO.status());
            assertEquals(taskCaptureValue.getPriority(), taskResponseDTO.priority());

            assertEquals(taskRequestDTO.title(), taskResponseDTO.title());
            assertEquals(taskRequestDTO.description(), taskResponseDTO.description());
            assertEquals(taskRequestDTO.status(), taskResponseDTO.status());
            assertEquals(taskRequestDTO.priority(), taskResponseDTO.priority());
        }

        @Test
        @DisplayName("Should create subtask with success")
        void shouldCreateSubtask() {
            //Arrange
            Users user = new Users();
            user.setId(UUID.randomUUID());
            user.setName("Andrey");
            user.setEmail("andrey@teste.com");
            user.setPassword("admin123");

            UUID parentTaskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

            Task taskParent = new Task();
            taskParent.setId(parentTaskId);
            taskParent.setTitle("Title Parent");
            taskParent.setDescription("Description Parent");
            taskParent.setDueDate(ZonedDateTime.now().plusDays(3));
            taskParent.setStatus(TaskStatus.IN_PROGRESS);
            taskParent.setPriority(TaskPriority.MEDIUM);

            TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                    "Title Test",
                    "Description test",
                    ZonedDateTime.now().plusDays(3),
                    TaskStatus.PENDING,
                    TaskPriority.HIGH,
                    parentTaskId
            );

            when(authentication.getName()).thenReturn("andrey@teste.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(taskRepository.findById(parentTaskId)).thenReturn(Optional.of(taskParent));
            when(taskRepository.save(any(Task.class))).thenAnswer(
                    invocationOnSave -> invocationOnSave.getArgument(0)
            );

            //Act
            TaskResponseDTO taskResponseDTO = taskService.createTask(taskRequestDTO);

            //Assert
            verify(userRepository, times(1)).findByEmail(user.getEmail());
            verify(taskRepository, times(1)).findById(parentTaskId);
            verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());

            var taskCaptureValue = taskArgumentCaptor.getValue();

            assertNotNull(taskCaptureValue);
            assertNotNull(taskResponseDTO);
            assertNotNull(taskResponseDTO.parentTaskId());

            assertEquals(parentTaskId, taskResponseDTO.parentTaskId());
            assertEquals(user, taskCaptureValue.getUser());

            assertEquals(taskCaptureValue.getTitle(), taskResponseDTO.title());
            assertEquals(taskCaptureValue.getDescription(), taskResponseDTO.description());
            assertEquals(taskCaptureValue.getStatus(), taskResponseDTO.status());
            assertEquals(taskCaptureValue.getPriority(), taskResponseDTO.priority());

            assertEquals(taskRequestDTO.title(), taskResponseDTO.title());
            assertEquals(taskRequestDTO.description(), taskResponseDTO.description());
            assertEquals(taskRequestDTO.status(), taskResponseDTO.status());
            assertEquals(taskRequestDTO.priority(), taskResponseDTO.priority());
        }

        @Test
        @DisplayName("Should throw exception case user not found")
        void shouldThrowExceptionCase1() {
            //Arrange
            Users user = new Users();
            user.setId(UUID.randomUUID());
            user.setName("Andrey");
            user.setEmail("andrey@teste.com");
            user.setPassword("admin123");

            TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                    "Title Test",
                    "Description test",
                    ZonedDateTime.now().plusDays(3),
                    TaskStatus.PENDING,
                    TaskPriority.HIGH,
                    null
            );

            when(authentication.getName()).thenReturn("andrey@teste.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UsersException.class, () -> taskService.createTask(taskRequestDTO));

            verify(userRepository, times(1)).findByEmail(user.getEmail());
            verify(taskRepository, times(0)).findById(any(UUID.class));
            verify(taskRepository, times(0)).save(any(Task.class));
        }

        @Test
        @DisplayName("Should throw exception case task not found")
        void shouldThrowExceptionCase2() {
            //Arrange
            Users user = new Users();
            user.setId(UUID.randomUUID());
            user.setName("Andrey");
            user.setEmail("andrey@teste.com");
            user.setPassword("admin123");

            UUID parentTaskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

            TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                    "Title Test",
                    "Description test",
                    ZonedDateTime.now().plusDays(3),
                    TaskStatus.PENDING,
                    TaskPriority.HIGH,
                    parentTaskId
            );

            when(authentication.getName()).thenReturn("andrey@teste.com");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(taskRepository.findById(parentTaskId)).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(TaskException.class, () -> taskService.createTask(taskRequestDTO));

            verify(userRepository, times(1)).findByEmail(user.getEmail());
            verify(taskRepository, times(1)).findById(parentTaskId);
            verify(taskRepository, times(0)).save(any(Task.class));
        }
    }

    @Nested
    class updateStatusTask {

        @Test
        @DisplayName("Should update status task with success")
        void shouldUpdateStatusTask() {
            //Arrange
            UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            TaskStatus newStatus = TaskStatus.IN_PROGRESS;

            Task taskParent = new Task();
            taskParent.setId(taskId);
            taskParent.setTitle("Title Parent");
            taskParent.setDescription("Description Parent");
            taskParent.setDueDate(ZonedDateTime.now().plusDays(3));
            taskParent.setStatus(TaskStatus.PENDING);
            taskParent.setPriority(TaskPriority.MEDIUM);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskParent));
            when(taskRepository.save(any(Task.class))).thenAnswer(
                    invocationOnSave -> invocationOnSave.getArgument(0)
            );

            //Act
            TaskResponseDTO taskResponseDTO = taskService.updateStatus(taskId, newStatus);

            //Assert
            verify(taskRepository, times(1)).findById(taskId);

            verify(taskRepository, times(1)).save(taskArgumentCaptor.capture());
            var taskCaptureValue = taskArgumentCaptor.getValue();

            assertNotNull(taskResponseDTO);

            assertEquals(taskId, taskCaptureValue.getId());
            assertEquals(taskId, taskResponseDTO.id());
            assertEquals(newStatus, taskCaptureValue.getStatus());

            assertEquals(taskCaptureValue.getTitle(), taskResponseDTO.title());
            assertEquals(taskCaptureValue.getDescription(), taskResponseDTO.description());
            assertEquals(taskCaptureValue.getStatus(), taskResponseDTO.status());
            assertEquals(taskCaptureValue.getPriority(), taskResponseDTO.priority());
        }

        @Test
        @DisplayName("Should throw exception case task not found")
        void shouldThrowExceptionCase1() {
            //Arrange
            UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            TaskStatus newStatus = TaskStatus.COMPLETED;

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(TaskException.class, () -> taskService.updateStatus(taskId, newStatus));

            verify(taskRepository,times(1)).findById(taskId);
            verify(taskRepository, times(0)).findByParentTask(any(Task.class));
            verify(taskRepository, times(0)).save(any(Task.class));
        }

        @Test
        @DisplayName("Should throw exception if existing tasks have status equal to COMPLETED")
        void shouldThrowExceptionCase2() {
            //Arrange
            UUID taskId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            TaskStatus newStatus = TaskStatus.COMPLETED;

            Task taskParent = new Task();
            taskParent.setId(taskId);
            taskParent.setTitle("Title Parent");
            taskParent.setDescription("Description Parent");
            taskParent.setDueDate(ZonedDateTime.now().plusDays(3));
            taskParent.setStatus(TaskStatus.PENDING);
            taskParent.setPriority(TaskPriority.MEDIUM);

            Task subtask1 = new Task();
            subtask1.setId(UUID.randomUUID());
            subtask1.setTitle("Title Parent");
            subtask1.setDescription("Description Parent");
            subtask1.setDueDate(ZonedDateTime.now().plusDays(3));
            subtask1.setStatus(TaskStatus.PENDING);
            subtask1.setPriority(TaskPriority.MEDIUM);
            subtask1.setParentTask(taskParent);

            Task subtask2 = new Task();
            subtask2.setId(UUID.randomUUID());
            subtask2.setTitle("Title Parent");
            subtask2.setDescription("Description Parent");
            subtask2.setDueDate(ZonedDateTime.now().plusDays(3));
            subtask2.setStatus(TaskStatus.PENDING);
            subtask2.setPriority(TaskPriority.MEDIUM);
            subtask2.setParentTask(taskParent);

            var listSubtasks = List.of(subtask1, subtask2);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskParent));
            when(taskRepository.findByParentTask(taskParent)).thenAnswer(
                    invocationOnFindByParentTask -> listSubtasks.stream()
                            .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
                            .toList()
            );

            //Act & Assert
            assertThrows(TaskException.class, () -> taskService.updateStatus(taskId, newStatus));

            verify(taskRepository, times(1)).findById(taskId);
            verify(taskRepository, times(1)).findByParentTask(taskParent);
            verify(taskRepository, times(0)).save(any(Task.class));
        }
    }
}