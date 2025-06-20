package com.bulhakov.commands;

import com.bulhakov.annotations.CommandMapping;
import com.bulhakov.exceptions.WrongDateException;
import com.bulhakov.model.User;
import com.bulhakov.services.UserService;
import com.bulhakov.util.LocalizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@CommandMapping(name = "/birthday")
public class SetBirthdayCommand extends AbstractCommand {

    private static final String DATE_STRING_FORMAT = "dd.mm.yyyy";

    private final SimpleDateFormat dateFormat;
    private final LocalizationManager localizationManager;

    @Autowired
    public SetBirthdayCommand(LocalizationManager localizationManager, UserService service) {
        super(localizationManager);
        this.userService = service;
        dateFormat = new SimpleDateFormat(DATE_STRING_FORMAT);
        dateFormat.setLenient(false);
        this.localizationManager = localizationManager;
    }

    @Override
    public void processUpdate(Update update, TelegramLongPollingBot controller) {
        String messageText = update.getMessage().getText();
        String[] words = messageText.split(" ");
        SendMessage answer;
        if (words.length == 1) {
            answer = getAnswer(update.getMessage(),
                    localizationManager.getStringFromResource("BIRTHDAY_INFO"));
            execute(controller, answer);
            return;
        }

        String dateString = words[1];

        long userId = update.getMessage().getFrom().getId();
        User user = userService.findUser(String.valueOf(userId));
        if (user == null) {
            answer = getAnswer(update.getMessage(),
                    localizationManager.getStringFromResource("UNKNOWN_USER"));
            execute(controller, answer);
            return;
        }

        try {
            user.setBirthday(getBirthdayDate(dateString));
            userService.updateUser(user);
            answer = getAnswer(update.getMessage(),
                    localizationManager.getStringFromResource("BIRTHDAY_ACCEPTED"));
            execute(controller, answer);
        } catch (ParseException e) {
            answer = getAnswer(update.getMessage(),
                    localizationManager.getStringFromResource("INVALID_DATE_FORMAT"));
            execute(controller, answer);
            e.printStackTrace();
        } catch (WrongDateException e) {
            String description = null;
            WrongDateException.CAUSE cause = e.getDateExceptionCause();
            if (cause == WrongDateException.CAUSE.LATE) {
                description = localizationManager.getStringFromResource("LATE_DATE");
            }else if(cause == WrongDateException.CAUSE.FUTURE) {
                description = localizationManager.getStringFromResource("FUTURE_DATE");
            }
            answer = getAnswer(update.getMessage(), description);
            execute(controller, answer);
        }
    }


    private Date getBirthdayDate(String dateString) throws ParseException, WrongDateException {
        final Date oldDate = dateFormat.parse("01.01.1900");
        final Date newDate = new Date(System.currentTimeMillis());

        Date birthday = dateFormat.parse(dateString);
        if (birthday.compareTo(oldDate) < 1) {
            throw new WrongDateException(WrongDateException.CAUSE.LATE);
        } else if (birthday.compareTo(newDate) >= 1) {
            throw new WrongDateException(WrongDateException.CAUSE.FUTURE);
        }
        return birthday;
    }
}
