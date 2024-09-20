package yevhen.synii.admin_panel.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailIsAlreadyTaken extends RuntimeException {
    private String message;
}
