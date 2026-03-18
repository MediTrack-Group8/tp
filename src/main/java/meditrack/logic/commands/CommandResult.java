package meditrack.logic.commands;

import java.util.Objects;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = Objects.requireNonNull(feedbackToUser);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }
}