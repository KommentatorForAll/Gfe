/**
 * an interface, which makes Classes able to be called every tick by an engine.
 */
public interface Tickable {

    /**
     * the super methode, for ticking. this is the one being called every tick.
     * should be final and call the tick() method. This is a way to call functions in super classes without having to be manually called in the objects
     */
    void _tick();

    /**
     * function which gets called every tick for objects and worlds
     */
    void tick();

}
