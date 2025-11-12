package naumen.project.entity.enums;

/**
 * Статусы заказа
 */
public enum OrderStatus {

    /**
     * Заказ создан
     */
    CREATED,

    /**
     * Заказ принят рестораном
     */
    ACCEPTED,

    /**
     * Заказ приготовлен
     */
    PREPARED,

    /**
     * Заказ забран курьером
     */
    DELIVERING,

    /**
     * Заказ доставлен
     */
    COMPLETED,
}
