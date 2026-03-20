package meditrack.logic.commands.personnel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.Personnel;

/**
 * Generates a randomised duty roster from all currently FIT personnel.
 *
 * <p>This command is <b>stateless</b>: it does not persist the roster to the model,
 * and each execution produces a fresh shuffle. The roster is stored in
 * {@link #getLastRoster()} for the UI to read after execution.
 *
 * <p>Validation rule: at least one FIT personnel must exist — enforced by
 * {@link ModelManager#generateRoster()}, which throws {@link CommandException}
 * if the FIT list is empty.
 */
public class GenerateRosterCommand extends Command {

    public static final String COMMAND_WORD = "generate_roster";

    public static final String MESSAGE_SUCCESS_HEADER = "Duty Roster — %d FIT personnel:";
    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Generates a randomised duty roster from all FIT personnel.\n"
                    + "No parameters required.\n"
                    + "Example: " + COMMAND_WORD;

    /** Stores the last generated roster so the UI can retrieve it without re-executing. */
    private List<Personnel> lastRoster;

    @Override
    public CommandResult execute(Model model) throws CommandException {
        ModelManager manager = (ModelManager) model;
        lastRoster = manager.generateRoster(); // throws CommandException if no FIT personnel

        String header = String.format(MESSAGE_SUCCESS_HEADER, lastRoster.size());
        String numberedList = IntStream.range(0, lastRoster.size())
                .mapToObj(i -> (i + 1) + ". " + lastRoster.get(i).getName())
                .collect(Collectors.joining("\n"));

        return new CommandResult(header + "\n" + numberedList);
    }

    /**
     * Returns the roster produced by the most recent {@link #execute(Model)} call,
     * or {@code null} if this command has not yet been executed.
     */
    public List<Personnel> getLastRoster() {
        return lastRoster;
    }
}