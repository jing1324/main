package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.Statistics.clearStatistics;
import static seedu.address.logic.commands.Statistics.updateStatistics;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_HEALTHWORKERS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_REQUESTS;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyRequestBook;
import seedu.address.model.request.Request;
import seedu.address.model.tag.Condition;

import javafx.collections.ObservableList;

import java.util.Set;

/**
 * Reverts the {@code model}'s address book to its previously undone state.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Redo success!";
    public static final String MESSAGE_FAILURE = "No more commands to redo!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (!model.canRedo()) {
            throw new CommandException(MESSAGE_FAILURE);
        }

        model.redo();
        model.updateFilteredHealthWorkerList(PREDICATE_SHOW_ALL_HEALTHWORKERS);
        model.updateFilteredRequestList(PREDICATE_SHOW_ALL_REQUESTS);

        ReadOnlyRequestBook requestBook = model.getRequestBook();
        ObservableList<Request> requestList = requestBook.getRequestList();
        clearStatistics();
        for (Request request : requestList) {
            Set<Condition> conditionSet = request.getConditions();
            updateStatistics(conditionSet);
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
