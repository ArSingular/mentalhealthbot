package dev.kuch.mental_health_support.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Korol Artur
 * 19.06.2025
 */

public class CalendarBuilder {

    public static InlineKeyboardMarkup buildCalendar(int year, int month) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                btn("Пн", "ignore"), btn("Вт", "ignore"), btn("Ср", "ignore"),
                btn("Чт", "ignore"), btn("Пт", "ignore"), btn("Сб", "ignore"), btn("Нд", "ignore")
        ));

        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        int lengthOfMonth = ym.lengthOfMonth();
        int shift = (firstDay.getDayOfWeek().getValue() + 6) % 7;

        LocalDate today = LocalDate.now();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < shift; i++) {
            row.add(btn(" ", "ignore"));
        }

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate current = LocalDate.of(year, month, day);
            String label = String.valueOf(day);

            // 🔵 Підсвітка сьогоднішнього дня
            if (current.equals(today)) {
                label = "🔵" + day;
            }

            String callback = "calendar_" + year + "_" + month + "_" + day;
            row.add(btn(label, callback));

            if (row.size() == 7) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }

        if (!row.isEmpty()) {
            rows.add(row);
        }

        int prevMonth = (month == 1) ? 12 : month - 1;
        int prevYear = (month == 1) ? year - 1 : year;

        int nextMonth = (month == 12) ? 1 : month + 1;
        int nextYear = (month == 12) ? year + 1 : year;

        String prevLabel = "⬅️ " + getMonthName(prevMonth);
        String nextLabel = getMonthName(nextMonth) + " ➡️";

        rows.add(List.of(
                btn(prevLabel, "calendar_prev_" + prevYear + "_" + prevMonth),
                btn("📅 " + getMonthName(month) + " " + year, "ignore"),
                btn(nextLabel, "calendar_next_" + nextYear + "_" + nextMonth)
        ));

        return new InlineKeyboardMarkup(rows);
    }

    private static InlineKeyboardButton btn(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    private static String getMonthName(int month) {
        return switch (month) {
            case 1 -> "Січень";
            case 2 -> "Лютий";
            case 3 -> "Березень";
            case 4 -> "Квітень";
            case 5 -> "Травень";
            case 6 -> "Червень";
            case 7 -> "Липень";
            case 8 -> "Серпень";
            case 9 -> "Вересень";
            case 10 -> "Жовтень";
            case 11 -> "Листопад";
            case 12 -> "Грудень";
            default -> "";
        };
    }

}
