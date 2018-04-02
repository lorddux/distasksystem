package ru.hse.lorddux.structures;

import lombok.Data;
import lombok.ToString;

/**
 * Task returned by remote storage
 * Example:
 * <QueueMessage>
 * <MessageId>5974b586-0df3-4e2d-ad0c-18e3892bfca2</MessageId>
 * <InsertionTime>Fri, 09 Oct 2009 21:04:30 GMT</InsertionTime>
 * <ExpirationTime>Fri, 16 Oct 2009 21:04:30 GMT</ExpirationTime>
 * <PopReceipt>YzQ4Yzg1MDItYTc0Ny00OWNjLTkxYTUtZGM0MDFiZDAwYzEw</PopReceipt>
 * <TimeNextVisible>Fri, 09 Oct 2009 23:29:20 GMT</TimeNextVisible>
 * <DequeueCount>1</DequeueCount>
 * <MessageText>PHRlc3Q+dGhpcyBpcyBhIHRlc3QgbWVzc2FnZTwvdGVzdD4=</MessageText>
 * </QueueMessage>
 */
@Data
@ToString
public class TaskItem {
    private String messageId;
    private String insertionTime;
    private String expirationTime;
    private String popReceipt;
    private String timeNextVisible;
    private String messageText;
    private Integer dequeueCount;

}