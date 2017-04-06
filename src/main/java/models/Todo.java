package models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Builder
@Data
public class Todo {

    private String uuid;
    private String name;
    private String description;
    private Date dueDate;
    private String createdBy;
}
