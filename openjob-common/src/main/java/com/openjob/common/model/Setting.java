package com.openjob.common.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Setting {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    protected String id;

    @Column(unique = true, nullable = false)
    @Size(max = 32)
    private String name;

    @Column(unique = true, nullable = false, columnDefinition = "text")
    private String value;

    @Column(columnDefinition = "text")
    private String extraValue;

    public Setting() {
    }

    public String getId() {
        return this.id;
    }

    public @Size(max = 32) String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getExtraValue() {
        return this.extraValue;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(@Size(max = 32) String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setExtraValue(String extraValue) {
        this.extraValue = extraValue;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Setting)) return false;
        final Setting other = (Setting) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$value = this.getValue();
        final Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        final Object this$extraValue = this.getExtraValue();
        final Object other$extraValue = other.getExtraValue();
        if (this$extraValue == null ? other$extraValue != null : !this$extraValue.equals(other$extraValue))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Setting;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        final Object $extraValue = this.getExtraValue();
        result = result * PRIME + ($extraValue == null ? 43 : $extraValue.hashCode());
        return result;
    }

    public String toString() {
        return "Setting(id=" + this.getId() + ", name=" + this.getName() + ", value=" + this.getValue() + ", extraValue=" + this.getExtraValue() + ")";
    }
}
