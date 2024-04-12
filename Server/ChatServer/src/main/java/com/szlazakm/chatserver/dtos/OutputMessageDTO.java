package com.szlazakm.chatserver.dtos;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OutputMessageDTO extends MessageDTO {

    String date;

    public OutputMessageDTO(final String from, final String to, final String text, final String date) {
        this.setFrom(from);
        this.setTo(to);
        this.setText(text);
        this.date = date;
    }
}
