package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORGANIZATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILLS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FilterHealthWorkerCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.healthworker.HealthWorker;
import seedu.address.model.tag.Specialisation;

/**
 * Parses input arguments and creates a new FilterHealthWorkerCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    public static final String MESSAGE_USAGE = FilterCommand.INVALID_MESSAGE_FORMAT + "\n"
            + FilterHealthWorkerCommand.MESSAGE_USAGE;

    public static final String PREAMBLE_WHITESPACE = " ";

    /**
     * Parses the given {@code String} of arguments in the context of the FilterHealthWorkerCommand
     * and returns an FilterHealthWorkerCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        CommandMode commandMode = ArgumentTokenizer.checkMode(trimmedArgs);
        if (commandMode == CommandMode.HEALTH_WORKER) {
            try {
                return new FilterHealthWorkerCommand(parseHealthWorkerPredicates(PREAMBLE_WHITESPACE
                        + ArgumentTokenizer.trimMode(args)));
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
            }
        } else if (commandMode == CommandMode.REQUEST) {

        }

        throw new ParseException(String.format(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                MESSAGE_USAGE)));
    }

    /**
     * Method that parses a string of parameters into a list of predicates for filtering health workes in a list.
     */
    public static List<Predicate> parseHealthWorkerPredicates(String args) throws ParseException {
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME,
                PREFIX_SKILLS, PREFIX_ORGANIZATION);

        if (!anyPrefixPresent(argumentMultimap, PREFIX_NAME,
                PREFIX_SKILLS, PREFIX_ORGANIZATION)) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    FilterHealthWorkerCommand.MESSAGE_USAGE));
        }

        List<Predicate> predicateList = new ArrayList<>();

        if (argumentMultimap.getValue(PREFIX_NAME).isPresent()) {
            predicateList.add(x -> ((HealthWorker) x).getName().contains(argumentMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argumentMultimap.getValue(PREFIX_ORGANIZATION).isPresent()) {
            predicateList.add(x -> ((HealthWorker) x).getOrganization().contains(argumentMultimap
                    .getValue(PREFIX_ORGANIZATION).get()));
        }
        if (argumentMultimap.getValue(PREFIX_SKILLS).isPresent()) {
            Set<Specialisation> otherSkills = new HashSet<>();
            for (String s : argumentMultimap.getAllValues(PREFIX_SKILLS)) {
                if (Specialisation.isValidSpecialisation(s)) {
                    otherSkills.add(Specialisation.parseString(s));
                }
            }
            predicateList.add(x -> ((HealthWorker) x).getSkills().containsAll(otherSkills));
        }

        return predicateList;
    }

    /**
     * Method to check if there are any existing prefixes in the ArgumentMultiMap.
     */
    private static boolean anyPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
