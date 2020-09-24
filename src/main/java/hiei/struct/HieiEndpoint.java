package hiei.struct;

import hiei.HieiServer;

abstract public class HieiEndpoint {
    protected final HieiServer hiei;

    public HieiEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public abstract void execute(HieiEndpointContext context);
}
