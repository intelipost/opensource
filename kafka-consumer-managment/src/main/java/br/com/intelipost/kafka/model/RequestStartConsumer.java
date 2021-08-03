package br.com.intelipost.kafka.model;

import java.util.Optional;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

public class RequestStartConsumer extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {
	private static final long serialVersionUID = 5500222940097291096L;
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
			"{\"type\":\"record\",\"name\":\"RequestStartConsumer\",\"namespace\":\"br.com.intelipost.kafka.model\",\"fields\":[{\"name\":\"consumerId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");

	public static org.apache.avro.Schema getClassSchema() {
		return SCHEMA$;
	}

	private static SpecificData MODEL$ = new SpecificData();

	private static final BinaryMessageEncoder<RequestStartConsumer> ENCODER = new BinaryMessageEncoder<RequestStartConsumer>(
			MODEL$, SCHEMA$);

	private static final BinaryMessageDecoder<RequestStartConsumer> DECODER = new BinaryMessageDecoder<RequestStartConsumer>(
			MODEL$, SCHEMA$);

	/**
	 * Return the BinaryMessageEncoder instance used by this class.
	 * 
	 * @return the message encoder used by this class
	 */
	public static BinaryMessageEncoder<RequestStartConsumer> getEncoder() {
		return ENCODER;
	}

	/**
	 * Return the BinaryMessageDecoder instance used by this class.
	 * 
	 * @return the message decoder used by this class
	 */
	public static BinaryMessageDecoder<RequestStartConsumer> getDecoder() {
		return DECODER;
	}

	/**
	 * Create a new BinaryMessageDecoder instance for this class that uses the
	 * specified {@link SchemaStore}.
	 * 
	 * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
	 * @return a BinaryMessageDecoder instance for this class backed by the given
	 *         SchemaStore
	 */
	public static BinaryMessageDecoder<RequestStartConsumer> createDecoder(SchemaStore resolver) {
		return new BinaryMessageDecoder<RequestStartConsumer>(MODEL$, SCHEMA$, resolver);
	}

	/**
	 * Serializes this RequestStartConsumer to a ByteBuffer.
	 * 
	 * @return a buffer holding the serialized data for this instance
	 * @throws java.io.IOException if this instance could not be serialized
	 */
	public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
		return ENCODER.encode(this);
	}

	/**
	 * Deserializes a RequestStartConsumer from a ByteBuffer.
	 * 
	 * @param b a byte buffer holding serialized data for an instance of this class
	 * @return a RequestStartConsumer instance decoded from the given buffer
	 * @throws java.io.IOException if the given bytes could not be deserialized into
	 *                             an instance of this class
	 */
	public static RequestStartConsumer fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
		return DECODER.decode(b);
	}

	private java.lang.String consumerId;

/**
* Default constructor.  Note that this does not initialize fields
* to their default values from the schema.  If that is desired then
* one should use <code>newBuilder()</code>.
*/
public RequestStartConsumer() {}

/**
* All-args constructor.
* @param consumerId The new value for consumerId
*/
public RequestStartConsumer(java.lang.String consumerId) {
this.consumerId = consumerId;
}

	public org.apache.avro.specific.SpecificData getSpecificData() {
		return MODEL$;
	}

	public org.apache.avro.Schema getSchema() {
		return SCHEMA$;
	}

// Used by DatumWriter. Applications should not call.
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return consumerId;
		default:
			throw new IndexOutOfBoundsException("Invalid index: " + field$);
		}
	}

// Used by DatumReader. Applications should not call.
	@SuppressWarnings(value = "unchecked")
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			consumerId = value$ != null ? value$.toString() : null;
			break;
		default:
			throw new IndexOutOfBoundsException("Invalid index: " + field$);
		}
	}

	/**
	 * Gets the value of the 'consumerId' field.
	 * 
	 * @return The value of the 'consumerId' field.
	 */
	public java.lang.String getConsumerId() {
		return consumerId;
	}

	/**
	 * Gets the value of the 'consumerId' field as an
	 * Optional&lt;java.lang.String&gt;.
	 * 
	 * @return The value wrapped in an Optional&lt;java.lang.String&gt;.
	 */
	public Optional<java.lang.String> getOptionalConsumerId() {
		return Optional.<java.lang.String>ofNullable(consumerId);
	}

	/**
	 * Sets the value of the 'consumerId' field.
	 * 
	 * @param value the value to set.
	 */
	public void setConsumerId(java.lang.String value) {
		this.consumerId = value;
	}

	/**
	 * Creates a new RequestStartConsumer RecordBuilder.
	 * 
	 * @return A new RequestStartConsumer RecordBuilder
	 */
	public static br.com.intelipost.kafka.model.RequestStartConsumer.Builder newBuilder() {
		return new br.com.intelipost.kafka.model.RequestStartConsumer.Builder();
	}

	/**
	 * Creates a new RequestStartConsumer RecordBuilder by copying an existing
	 * Builder.
	 * 
	 * @param other The existing builder to copy.
	 * @return A new RequestStartConsumer RecordBuilder
	 */
	public static br.com.intelipost.kafka.model.RequestStartConsumer.Builder newBuilder(
			br.com.intelipost.kafka.model.RequestStartConsumer.Builder other) {
		if (other == null) {
			return new br.com.intelipost.kafka.model.RequestStartConsumer.Builder();
		} else {
			return new br.com.intelipost.kafka.model.RequestStartConsumer.Builder(other);
		}
	}

	/**
	 * Creates a new RequestStartConsumer RecordBuilder by copying an existing
	 * RequestStartConsumer instance.
	 * 
	 * @param other The existing instance to copy.
	 * @return A new RequestStartConsumer RecordBuilder
	 */
	public static br.com.intelipost.kafka.model.RequestStartConsumer.Builder newBuilder(
			br.com.intelipost.kafka.model.RequestStartConsumer other) {
		if (other == null) {
			return new br.com.intelipost.kafka.model.RequestStartConsumer.Builder();
		} else {
			return new br.com.intelipost.kafka.model.RequestStartConsumer.Builder(other);
		}
	}

	/**
	 * RecordBuilder for RequestStartConsumer instances.
	 */
	@org.apache.avro.specific.AvroGenerated
	public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<RequestStartConsumer>
			implements org.apache.avro.data.RecordBuilder<RequestStartConsumer> {

		private java.lang.String consumerId;

		/** Creates a new Builder */
		private Builder() {
			super(SCHEMA$);
		}

		/**
		 * Creates a Builder by copying an existing Builder.
		 * 
		 * @param other The existing Builder to copy.
		 */
		private Builder(br.com.intelipost.kafka.model.RequestStartConsumer.Builder other) {
			super(other);
			if (isValidValue(fields()[0], other.consumerId)) {
				this.consumerId = data().deepCopy(fields()[0].schema(), other.consumerId);
				fieldSetFlags()[0] = other.fieldSetFlags()[0];
			}
		}

		/**
		 * Creates a Builder by copying an existing RequestStartConsumer instance
		 * 
		 * @param other The existing instance to copy.
		 */
		private Builder(br.com.intelipost.kafka.model.RequestStartConsumer other) {
			super(SCHEMA$);
			if (isValidValue(fields()[0], other.consumerId)) {
				this.consumerId = data().deepCopy(fields()[0].schema(), other.consumerId);
				fieldSetFlags()[0] = true;
			}
		}

		/**
		 * Gets the value of the 'consumerId' field.
		 * 
		 * @return The value.
		 */
		public java.lang.String getConsumerId() {
			return consumerId;
		}

		/**
		 * Gets the value of the 'consumerId' field as an
		 * Optional&lt;java.lang.String&gt;.
		 * 
		 * @return The value wrapped in an Optional&lt;java.lang.String&gt;.
		 */
		public Optional<java.lang.String> getOptionalConsumerId() {
			return Optional.<java.lang.String>ofNullable(consumerId);
		}

		/**
		 * Sets the value of the 'consumerId' field.
		 * 
		 * @param value The value of 'consumerId'.
		 * @return This builder.
		 */
		public br.com.intelipost.kafka.model.RequestStartConsumer.Builder setConsumerId(java.lang.String value) {
			validate(fields()[0], value);
			this.consumerId = value;
			fieldSetFlags()[0] = true;
			return this;
		}

		/**
		 * Checks whether the 'consumerId' field has been set.
		 * 
		 * @return True if the 'consumerId' field has been set, false otherwise.
		 */
		public boolean hasConsumerId() {
			return fieldSetFlags()[0];
		}

		/**
		 * Clears the value of the 'consumerId' field.
		 * 
		 * @return This builder.
		 */
		public br.com.intelipost.kafka.model.RequestStartConsumer.Builder clearConsumerId() {
			consumerId = null;
			fieldSetFlags()[0] = false;
			return this;
		}

		@Override
		@SuppressWarnings("unchecked")
		public RequestStartConsumer build() {
			try {
				RequestStartConsumer record = new RequestStartConsumer();
				record.consumerId = fieldSetFlags()[0] ? this.consumerId : (java.lang.String) defaultValue(fields()[0]);
				return record;
			} catch (org.apache.avro.AvroMissingFieldException e) {
				throw e;
			} catch (java.lang.Exception e) {
				throw new org.apache.avro.AvroRuntimeException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static final org.apache.avro.io.DatumWriter<RequestStartConsumer> WRITER$ = (org.apache.avro.io.DatumWriter<RequestStartConsumer>) MODEL$
			.createDatumWriter(SCHEMA$);

	@Override
	public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
		WRITER$.write(this, SpecificData.getEncoder(out));
	}

	@SuppressWarnings("unchecked")
	private static final org.apache.avro.io.DatumReader<RequestStartConsumer> READER$ = (org.apache.avro.io.DatumReader<RequestStartConsumer>) MODEL$
			.createDatumReader(SCHEMA$);

	@Override
	public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
		READER$.read(this, SpecificData.getDecoder(in));
	}

	@Override
	protected boolean hasCustomCoders() {
		return true;
	}

	@Override
	public void customEncode(org.apache.avro.io.Encoder out) throws java.io.IOException {
		out.writeString(this.consumerId);

	}

	@Override
	public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
		org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
		if (fieldOrder == null) {
			this.consumerId = in.readString();

		} else {
			for (int i = 0; i < 1; i++) {
				switch (fieldOrder[i].pos()) {
				case 0:
					this.consumerId = in.readString();
					break;

				default:
					throw new java.io.IOException("Corrupt ResolvingDecoder.");
				}
			}
		}
	}

}
