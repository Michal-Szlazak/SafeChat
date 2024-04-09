package com.szlazakm.chatserver.dtos;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OutputMessageDTO extends MessageDTO {

    String time;

    public OutputMessageDTO(final String from, final String text, final String time) {
        this.setFrom(from);
        this.setText(text);
        this.time = time;
    }
}
