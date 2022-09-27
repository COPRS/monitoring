package eu.csgroup.coprs.monitoring.common.datamodel.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import eu.csgroup.coprs.monitoring.common.bean.AutoMergeableMap;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Product implements DefaultEntity, Serializable {
    @Transient
    @Serial
    private static final long serialVersionUID = -1088870334322071348L;


    @Id
    @SequenceGenerator(sequenceName="product_id_seq", name = "product_id_seq", allocationSize=1)
    @GeneratedValue(generator = "product_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String filename;

    @Type( type = "jsonb" )
    @Column(columnDefinition = "jsonb")
    private AutoMergeableMap custom;

    private String timelinessName;

    private int timelinessValueSeconds;

    private boolean endToEndProduct;

    private boolean duplicate;

    @Column(name = "t0_pdgs_date")
    private Instant t0PdgsDate;

    private Instant pripStorageDate;

    private boolean late;

    public Product(Long id, String filename, AutoMergeableMap custom, String timelinessName, int timelinessValueSeconds, boolean endToEndProduct, boolean duplicate, Instant t0PdgsDate, Instant pripStorageDate, boolean late) {
        this.id = id;
        this.filename = filename;
        this.custom = custom;
        this.timelinessName = timelinessName;
        this.timelinessValueSeconds = timelinessValueSeconds;
        this.endToEndProduct = endToEndProduct;
        this.duplicate = duplicate;
        this.t0PdgsDate = t0PdgsDate;
        this.pripStorageDate = pripStorageDate;
        this.late = late;
    }

    public Product() {
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    @Override
    public Product copy() {
        return this.toBuilder().build();
    }

    @Override
    public void resetId() {
        this.id = null;
    }


    public Long getId() {
        return this.id;
    }

    public String getFilename() {
        return this.filename;
    }

    public AutoMergeableMap getCustom() {
        return this.custom;
    }

    public String getTimelinessName() {
        return this.timelinessName;
    }

    public int getTimelinessValueSeconds() {
        return this.timelinessValueSeconds;
    }

    public boolean isEndToEndProduct() {
        return this.endToEndProduct;
    }

    public boolean isDuplicate() {
        return this.duplicate;
    }

    public Instant getT0PdgsDate() {
        return this.t0PdgsDate;
    }

    public Instant getPripStorageDate() {
        return this.pripStorageDate;
    }

    public boolean isLate() {
        return this.late;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setCustom(AutoMergeableMap custom) {
        this.custom = custom;
    }

    public void setTimelinessName(String timelinessName) {
        this.timelinessName = timelinessName;
    }

    public void setTimelinessValueSeconds(int timelinessValueSeconds) {
        this.timelinessValueSeconds = timelinessValueSeconds;
    }

    public void setEndToEndProduct(boolean endToEndProduct) {
        this.endToEndProduct = endToEndProduct;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public void setT0PdgsDate(Instant t0PdgsDate) {
        this.t0PdgsDate = t0PdgsDate;
    }

    public void setPripStorageDate(Instant pripStorageDate) {
        this.pripStorageDate = pripStorageDate;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Product)) return false;
        final Product other = (Product) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$filename = this.getFilename();
        final Object other$filename = other.getFilename();
        if (this$filename == null ? other$filename != null : !this$filename.equals(other$filename)) return false;
        final Object this$custom = this.getCustom();
        final Object other$custom = other.getCustom();
        if (this$custom == null ? other$custom != null : !this$custom.equals(other$custom)) return false;
        final Object this$timelinessName = this.getTimelinessName();
        final Object other$timelinessName = other.getTimelinessName();
        if (this$timelinessName == null ? other$timelinessName != null : !this$timelinessName.equals(other$timelinessName))
            return false;
        if (this.getTimelinessValueSeconds() != other.getTimelinessValueSeconds()) return false;
        if (this.isEndToEndProduct() != other.isEndToEndProduct()) return false;
        if (this.isDuplicate() != other.isDuplicate()) return false;
        final Object this$t0PdgsDate = this.getT0PdgsDate();
        final Object other$t0PdgsDate = other.getT0PdgsDate();
        if (this$t0PdgsDate == null ? other$t0PdgsDate != null : !this$t0PdgsDate.equals(other$t0PdgsDate))
            return false;
        final Object this$pripStorageDate = this.getPripStorageDate();
        final Object other$pripStorageDate = other.getPripStorageDate();
        if (this$pripStorageDate == null ? other$pripStorageDate != null : !this$pripStorageDate.equals(other$pripStorageDate))
            return false;
        if (this.isLate() != other.isLate()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Product;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $filename = this.getFilename();
        result = result * PRIME + ($filename == null ? 43 : $filename.hashCode());
        final Object $custom = this.getCustom();
        result = result * PRIME + ($custom == null ? 43 : $custom.hashCode());
        final Object $timelinessName = this.getTimelinessName();
        result = result * PRIME + ($timelinessName == null ? 43 : $timelinessName.hashCode());
        result = result * PRIME + this.getTimelinessValueSeconds();
        result = result * PRIME + (this.isEndToEndProduct() ? 79 : 97);
        result = result * PRIME + (this.isDuplicate() ? 79 : 97);
        final Object $t0PdgsDate = this.getT0PdgsDate();
        result = result * PRIME + ($t0PdgsDate == null ? 43 : $t0PdgsDate.hashCode());
        final Object $pripStorageDate = this.getPripStorageDate();
        result = result * PRIME + ($pripStorageDate == null ? 43 : $pripStorageDate.hashCode());
        result = result * PRIME + (this.isLate() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "Product(id=" + this.getId() + ", filename=" + this.getFilename() + ", custom=" + this.getCustom() + ", timelinessName=" + this.getTimelinessName() + ", timelinessValueSeconds=" + this.getTimelinessValueSeconds() + ", endToEndProduct=" + this.isEndToEndProduct() + ", duplicate=" + this.isDuplicate() + ", t0PdgsDate=" + this.getT0PdgsDate() + ", pripStorageDate=" + this.getPripStorageDate() + ", late=" + this.isLate() + ")";
    }

    public ProductBuilder toBuilder() {
        return new ProductBuilder().id(this.id).filename(this.filename).custom(this.custom).timelinessName(this.timelinessName).timelinessValueSeconds(this.timelinessValueSeconds).endToEndProduct(this.endToEndProduct).duplicate(this.duplicate).t0PdgsDate(this.t0PdgsDate).pripStorageDate(this.pripStorageDate).late(this.late);
    }

    public static class ProductBuilder {
        private Long id;
        private String filename;
        private AutoMergeableMap custom;
        private String timelinessName;
        private int timelinessValueSeconds;
        private boolean endToEndProduct;
        private boolean duplicate;
        private Instant t0PdgsDate;
        private Instant pripStorageDate;
        private boolean late;

        ProductBuilder() {
        }

        public ProductBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProductBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public ProductBuilder custom(AutoMergeableMap custom) {
            this.custom = custom;
            return this;
        }

        public ProductBuilder timelinessName(String timelinessName) {
            this.timelinessName = timelinessName;
            return this;
        }

        public ProductBuilder timelinessValueSeconds(int timelinessValueSeconds) {
            this.timelinessValueSeconds = timelinessValueSeconds;
            return this;
        }

        public ProductBuilder endToEndProduct(boolean endToEndProduct) {
            this.endToEndProduct = endToEndProduct;
            return this;
        }

        public ProductBuilder duplicate(boolean duplicate) {
            this.duplicate = duplicate;
            return this;
        }

        public ProductBuilder t0PdgsDate(Instant t0PdgsDate) {
            this.t0PdgsDate = t0PdgsDate;
            return this;
        }

        public ProductBuilder pripStorageDate(Instant pripStorageDate) {
            this.pripStorageDate = pripStorageDate;
            return this;
        }

        public ProductBuilder late(boolean late) {
            this.late = late;
            return this;
        }

        public Product build() {
            return new Product(id, filename, custom, timelinessName, timelinessValueSeconds, endToEndProduct, duplicate, t0PdgsDate, pripStorageDate, late);
        }

        public String toString() {
            return "Product.ProductBuilder(id=" + this.id + ", filename=" + this.filename + ", custom=" + this.custom + ", timelinessName=" + this.timelinessName + ", timelinessValueSeconds=" + this.timelinessValueSeconds + ", endToEndProduct=" + this.endToEndProduct + ", duplicate=" + this.duplicate + ", t0PdgsDate=" + this.t0PdgsDate + ", pripStorageDate=" + this.pripStorageDate + ", late=" + this.late + ")";
        }
    }
}
