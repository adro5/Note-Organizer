package macro.noteorganizer.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "noteorganizer-mobilehub-923302063-Notes")

public class NotesDO {
    private String _userId;
    private String _content;
    private String _creationDate;
    private String _noteId;
    private String _title;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexNames = {"NoteContent","DateSorted","NoteTitle",})
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBIndexRangeKey(attributeName = "content", globalSecondaryIndexName = "NoteContent")
    public String getContent() {
        return _content;
    }

    public void setContent(final String _content) {
        this._content = _content;
    }
    @DynamoDBIndexRangeKey(attributeName = "creationDate", globalSecondaryIndexName = "DateSorted")
    public String getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(final String _creationDate) {
        this._creationDate = _creationDate;
    }
    @DynamoDBAttribute(attributeName = "noteId")
    public String getNoteId() {
        return _noteId;
    }

    public void setNoteId(final String _noteId) {
        this._noteId = _noteId;
    }
    @DynamoDBIndexRangeKey(attributeName = "title", globalSecondaryIndexName = "NoteTitle")
    public String getTitle() {
        return _title;
    }

    public void setTitle(final String _title) {
        this._title = _title;
    }

}
