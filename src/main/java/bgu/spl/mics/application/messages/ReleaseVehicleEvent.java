package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {
    private Future<DeliveryVehicle> deliveryVehicleFuture;

    public ReleaseVehicleEvent(Future<DeliveryVehicle> deliveryVehicleFuture){
        this.deliveryVehicleFuture = deliveryVehicleFuture;
    }

    public Future<DeliveryVehicle> getDeliveryVehicleFuture(){
        return deliveryVehicleFuture;
    }
}
