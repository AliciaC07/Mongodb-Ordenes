package Models;

public class Supplier {
    private int id;
    private String productId;
    private int deliveryDate;
    private float price;

    public Supplier(int id, String productId, int deliveryDate, float price) {
        this.id = id;
        this.productId = productId;
        this.deliveryDate = deliveryDate;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(int deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
