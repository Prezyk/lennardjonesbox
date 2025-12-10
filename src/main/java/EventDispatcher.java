import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {

    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> eventsHandlers;

    private static EventDispatcher INSTANCE = new EventDispatcher();

    public EventDispatcher() {
        this.eventsHandlers = new HashMap<>();
    }

    public static EventDispatcher getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventDispatcher();
        }
        return INSTANCE;
    }

    public <T extends Event> void registerEventHandler(Class<T> eventType, Consumer<T> eventHandler) {
        List<Consumer<? extends Event>> eventHandlers = eventsHandlers.computeIfAbsent(eventType, k -> new ArrayList<>());
        eventHandlers.add(eventHandler);
    }

    public <T extends Event> void dispatchEvent(T event) {
        List<Consumer<? extends Event>> eventHandlers = eventsHandlers.get(event.getClass());
        eventHandlers.forEach(eventHandler -> ((Consumer<T>) eventHandler).accept(event));
    }
}
