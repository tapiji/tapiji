
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipselabs.e4.tapiji.logger.Log;



public class EventBrokerMock implements IEventBroker {

    private static final String TAG = EventBrokerMock.class.getSimpleName();

    @Override
    public boolean send(String topic, Object data) {
        Log.d(TAG, String.format("Topic send: %s", topic));
        return false;
    }

    @Override
    public boolean post(String topic, Object data) {
        Log.d(TAG, String.format("Topic post: %s", topic));
        return false;
    }


    @Override
    public boolean subscribe(String topic, org.osgi.service.event.EventHandler eventHandler) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean subscribe(String topic, String filter, org.osgi.service.event.EventHandler eventHandler,
                    boolean headless) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unsubscribe(org.osgi.service.event.EventHandler eventHandler) {
        // TODO Auto-generated method stub
        return false;
    }


}