package kanban.serialization;

import com.google.gson.*;
import kanban.model.Task;

import java.lang.reflect.Type;



public class TaskDeserializer implements JsonDeserializer<Task> {

    @Override
    public Task deserialize(JsonElement jsonElement, Type type,
                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject taskObject = jsonElement.getAsJsonObject();
      //  JsonElement taskTypeElement = animalObject.get(animalTypeElementName);
       /* Task task = new Task();
        task.setTitle (taskObject.get("title").getAsString());
task.setDescription(taskObject.get("description").getAsString());
task.setId(taskObject.get("id").getAsInt());
task.setStatus(taskObject.get("status").getAsJsonPrimitive());
*/
        return null;
    }
}
