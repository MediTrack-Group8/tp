package meditrack.logic.parser.personnel;

import meditrack.logic.commands.personnel.*;
import meditrack.logic.parser.exceptions.*;

import meditrack.model.ModelManager;
import meditrack.model.Status;

/**
 * Parses raw user-input strings into typed {@link Command} objects,
 * enforcing all validation rules for the Personnel domain.
 *
 * <p>All methods are stateless and may be used without instantiation
 * via the static factory methods.
 */
public class PersonnelParser {

    // -------------------------------------------------------------------------
    // ADD_PERSONNEL  — name non-empty, valid Status, no duplicate (checked by model)
    // -------------------------------------------------------------------------

    /**
     * Parses an ADD_PERSONNEL request.
     *
     * <p>Expected format: {@code add_personnel n/NAME s/STATUS}
     *
     * @param args the argument string (everything after the command word)
     * @return a ready-to-execute {@link AddPersonnelCommand}
     * @throws ParseException if name is blank or status is invalid
     */
    public static AddPersonnelCommand parseAddPersonnel(String args) throws ParseException {
        String name = extractPrefixValue(args, "n/");
        String statusRaw = extractPrefixValue(args, "s/");

        // Rule 1: name must be non-blank
        if (name == null || name.isBlank()) {
            throw new ParseException("Name must not be blank. Format: add_personnel n/NAME s/STATUS");
        }

        // Rule 2: status must map to a valid Status enum value
        Status status = parseStatus(statusRaw);

        // Note: duplicate check (Rule 3) is enforced by ModelManager.addPersonnel()
        return new AddPersonnelCommand(name.trim(), status);
    }

    // -------------------------------------------------------------------------
    // REMOVE_PERSONNEL  — index within bounds (bounds check delegated to model)
    // -------------------------------------------------------------------------

    /**
     * Parses a REMOVE_PERSONNEL request.
     *
     * <p>Expected format: {@code remove_personnel INDEX}
     *
     * @param args the argument string (everything after the command word)
     * @return a ready-to-execute {@link RemovePersonnelCommand}
     * @throws ParseException if index is not a positive integer
     */
    public static RemovePersonnelCommand parseRemovePersonnel(String args) throws ParseException {
        int index = parsePositiveInt(args.trim(),
                "Index must be a positive integer. Format: remove_personnel INDEX");
        // Bounds check is delegated to ModelManager.deletePersonnel()
        return new RemovePersonnelCommand(index);
    }

    // -------------------------------------------------------------------------
    // UPDATE_STATUS  — valid Status enum value
    // -------------------------------------------------------------------------

    /**
     * Parses an UPDATE_STATUS request.
     *
     * <p>Expected format: {@code update_status INDEX s/STATUS}
     *
     * @param args the argument string (everything after the command word)
     * @return a ready-to-execute {@link UpdateStatusCommand}
     * @throws ParseException if index is invalid or status string is not a valid enum value
     */
    public static UpdateStatusCommand parseUpdateStatus(String args) throws ParseException {
        // Split on first whitespace to separate index from prefix args
        String[] parts = args.trim().split("\\s+", 2);
        if (parts.length < 2) {
            throw new ParseException("Format: update_status INDEX s/STATUS");
        }

        int index = parsePositiveInt(parts[0],
                "Index must be a positive integer. Format: update_status INDEX s/STATUS");

        String statusRaw = extractPrefixValue(parts[1], "s/");
        Status status = parseStatus(statusRaw);

        return new UpdateStatusCommand(index, status);
    }

    // -------------------------------------------------------------------------
    // GENERATE_ROSTER  — at least one FIT personnel (checked by model)
    // -------------------------------------------------------------------------

    /**
     * Parses a GENERATE_ROSTER request. No arguments are required.
     * The pre-condition (at least one FIT personnel) is enforced by the model.
     *
     * @return a ready-to-execute {@link GenerateRosterCommand}
     */
    public static GenerateRosterCommand parseGenerateRoster() {
        return new GenerateRosterCommand();
    }

    // -------------------------------------------------------------------------
    // Shared helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts the value following a given prefix (e.g., "n/") in an args string.
     * Returns {@code null} if the prefix is not found.
     */
    private static String extractPrefixValue(String args, String prefix) {
        if (args == null) {
            return null;
        }
        int start = args.indexOf(prefix);
        if (start == -1) {
            return null;
        }
        start += prefix.length();
        // Value ends at next prefix-like pattern or end of string
        int end = args.length();
        // Scan for another prefix token (one letter + '/')
        for (int i = start + 1; i < args.length() - 1; i++) {
            if (args.charAt(i + 1) == '/' && Character.isLetter(args.charAt(i))) {
                end = i;
                break;
            }
        }
        return args.substring(start, end).trim();
    }

    /**
     * Parses a status string; throws a {@link ParseException} with a clear message on failure.
     */
    private static Status parseStatus(String raw) throws ParseException {
        if (raw == null || raw.isBlank()) {
            throw new ParseException(
                    "Status must not be blank. Valid values: FIT, UNFIT, ON_LEAVE");
        }
        try {
            return Status.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage() + " Valid values: FIT, UNFIT, ON_LEAVE");
        }
    }

    /**
     * Parses a positive integer; throws {@link ParseException} with the given message on failure.
     */
    private static int parsePositiveInt(String token, String errorMessage) throws ParseException {
        try {
            int value = Integer.parseInt(token);
            if (value < 1) {
                throw new ParseException(errorMessage);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ParseException(errorMessage);
        }
    }
}