package hiei.struct;

import hiei.HieiServer;
import hiei.endpoints.equipments.SearchEquipment;
import hiei.endpoints.ships.SearchShip;

public class HieiEndpointRouter {
    public final SearchShip searchShip;
    public final SearchEquipment searchEquipment;

    public HieiEndpointRouter(HieiServer hiei) {
        this.searchShip = new SearchShip(hiei);
        this.searchEquipment = new SearchEquipment(hiei);
    }
}
