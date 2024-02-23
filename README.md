# CoralProto
A fast, binary and garbage-free serialization framework with a simple schema definition language.

## Features
- Simple schema definition language with message type and subtype
- Fast parsing (or direct access without parsing)
- Strictly binary (big-endian)
- Ascii encoding for logging/debugging
- Garbage-free (no GC overhead)
- Primitive types (boolean, char, byte, short, int, long, float and double)
- Fixed byte and char arrays
- Variable byte and char arrays (VarChars and VarBytes)
- Enum fields (CharEnum, ShortEnum, IntEnum and TwoCharEnum)
- Fields can be made optional
- Repeating groups with nesting support (repeating groups inside repeating groups)
- Schema evolution by appending new (optional or non-optional) fields

## Schema Definition Language
```plain
    CLASSNAME = com.coralblocks.coralproto.example.PriceChangeMessage
    TYPE = P
    SUBTYPE = C
    
    symbolId: long
    symbolDesc: varchars(128)
    mqReqId: long!
    
    orders:
        side: boolean
        levelId: long!
        priceLevel: double
        qty: int
        legs:
          legId: int
          legDesc: chars(8)!
        orderId: long
    
    lastTradeQty: long!
    lastTradePrice: double!
```
- TYPE and SUBTYPE are mandatory
- An exclamation mark at the end of a field indicates that the field is optional
- Repeating groups are created through indentation
- The number between parenthesis for varchars (and varbytes) is the maximum allowed size/length
- The number between parenthesis for chars (and bytes) is the fixed size/length

**NOTE:** For convenience, you can place the schema specification inside the Java class so that when you execute its main method the class is updated with the generated source code of the message. You can see an example [here](https://github.com/coralblocks/CoralProto/blob/main/src/main/java/com/coralblocks/coralproto/example/PriceChangeMessage.java).

## Writting the Message Fields
```java
PriceChangeMessage proto = new PriceChangeMessage();

proto.symbolId.set(1111L);
proto.symbolDesc.set("IBM");
proto.mqReqId.markAsNotPresent();

    proto.orders.clear();

    proto.orders.nextElement();
    proto.orders.side.set(true);
    proto.orders.levelId.set(11111111L);
    proto.orders.priceLevel.set(200.15);
    proto.orders.qty.set(1000);

        proto.orders.legs.clear();
    
        proto.orders.legs.nextElement();
        proto.orders.legs.legId.set(1);
        proto.orders.legs.legDesc.markAsNotPresent();
        
        proto.orders.legs.nextElement();
        proto.orders.legs.legId.set(2);
        proto.orders.legs.legDesc.set("myLeg2  ");

    proto.orders.orderId.set(1234L);

    proto.orders.nextElement();
    proto.orders.side.set(false);
    proto.orders.levelId.set(22222222L);
    proto.orders.priceLevel.set(200.75);
    proto.orders.qty.set(800);
    
        proto.orders.legs.clear();
    
        proto.orders.legs.nextElement();
        proto.orders.legs.legId.set(1);
        proto.orders.legs.legDesc.set("myLeg1  ");
        
        proto.orders.legs.nextElement();
        proto.orders.legs.legId.set(2);
        proto.orders.legs.legDesc.markAsNotPresent();
    
    proto.orders.orderId.set(5678L);

proto.lastTradeQty.set(100);
proto.lastTradePrice.set(200.55);
```

## Reading the Message Fields
```java
Assert.assertEquals(PriceChangeMessage.TYPE, proto.getType());
Assert.assertEquals(PriceChangeMessage.SUBTYPE, proto.getSubtype());

Assert.assertEquals(1111L, proto.symbolId.get());
Assert.assertEquals("IBM", proto.symbolDesc.get().toString());
Assert.assertEquals(false, proto.mqReqId.isPresent());

Assert.assertEquals(2, proto.orders.getNumberOfElements());

proto.orders.beginIteration();

Assert.assertEquals(true, proto.orders.iterHasNext());
proto.orders.iterNext();

Assert.assertEquals(true, proto.orders.side.get());
Assert.assertEquals(11111111L, proto.orders.levelId.get());
Assert.assertTrue(200.15 == proto.orders.priceLevel.get());
Assert.assertEquals(1000, proto.orders.qty.get());

Assert.assertEquals(2, proto.orders.legs.getNumberOfElements());

proto.orders.legs.beginIteration();

Assert.assertEquals(true, proto.orders.legs.iterHasNext());
proto.orders.legs.iterNext();

Assert.assertEquals(1, proto.orders.legs.legId.get());
Assert.assertEquals(false, proto.orders.legs.legDesc.isPresent());

Assert.assertEquals(true, proto.orders.legs.iterHasNext());
proto.orders.legs.iterNext();

Assert.assertEquals(2, proto.orders.legs.legId.get());
Assert.assertEquals("myLeg2  ", proto.orders.legs.legDesc.get().toString());

Assert.assertEquals(false, proto.orders.legs.iterHasNext());

Assert.assertEquals(true, proto.orders.iterHasNext());
proto.orders.iterNext();

Assert.assertEquals(false, proto.orders.side.get());
Assert.assertEquals(22222222L, proto.orders.levelId.get());
Assert.assertTrue(200.75 == proto.orders.priceLevel.get());
Assert.assertEquals(800, proto.orders.qty.get());

Assert.assertEquals(2, proto.orders.legs.getNumberOfElements());

proto.orders.legs.beginIteration();

Assert.assertEquals(true, proto.orders.legs.iterHasNext());
proto.orders.legs.iterNext();

Assert.assertEquals(1, proto.orders.legs.legId.get());
Assert.assertEquals("myLeg1  ", proto.orders.legs.legDesc.get().toString());

Assert.assertEquals(true, proto.orders.legs.iterHasNext());
proto.orders.legs.iterNext();

Assert.assertEquals(2, proto.orders.legs.legId.get());
Assert.assertEquals(false, proto.orders.legs.legDesc.isPresent());

Assert.assertEquals(false, proto.orders.legs.iterHasNext());

Assert.assertEquals(false, proto.orders.iterHasNext());

Assert.assertEquals(100, proto.lastTradeQty.get());
Assert.assertTrue(200.55 == proto.lastTradePrice.get());
```
**NOTE:** The full automated test for the PriceChangeMessage can be seen [here](https://github.com/coralblocks/CoralProto/blob/main/src/test/java/com/coralblocks/coralproto/PriceChangeMessageTest.java).

## Writting to and Reading from a ByteBuffer
```java
PriceChangeMessage proto = new PriceChangeMessage();

proto.symbolId.set(1111L);

ByteBuffer bb = ByteBuffer.allocate(1024);
proto.write(bb);
bb.flip();

PriceChangeMessage received = new PriceChangeMessage();

received.read(bb);

Assert.assertTrue(received.equals(proto));
Assert.assertEquals(proto.orders.symbolId.get(), received.orders.symbolId.get());
```

## Using a ProtoParser
```java
public static class MyProtoParser extends ProtoParser {

    @Override
    protected Proto[] defineProtoMessages() {
        return new Proto[] {
                new ProtoMessage1(),
                new ProtoMessage2()
        };
    }
}

ProtoParser protoParser = new MyProtoParser();

Proto proto = protoParser.parse(byteBuffer);

if (proto == null) throw new RuntimeException("Cannot parse ByteBuffer to Proto!");

char type = proto.getType();
char subtype = proto.getSubtype();

if (type == ProtoMessage1.TYPE && subtype == ProtoMessage1.SUBTYPE) {
    ProtoMessage1 protoMessage1 = (ProtoMessage1) proto;
    // access the ProtoMessage1 fields and be happy...
} else if (type == ProtoMessage2.TYPE && subtype == ProtoMessage2.SUBTYPE) {
    ProtoMessage2 protoMessage2 = (ProtoMessage2) proto;
    // access the ProtoMessage2 fields and be happy...
} else {
    throw new RuntimeException("Got a proto that I don't know how to handle: " + proto);
}
```

## Using Enum Fields
You should provide enumerations that implement [CharEnum](https://github.com/coralblocks/CoralProto/blob/main/src/main/java/com/coralblocks/coralproto/enums/CharEnum.java), [ShortEnum](https://github.com/coralblocks/CoralProto/blob/main/src/main/java/com/coralblocks/coralproto/enums/ShortEnum.java), [IntEnum](https://github.com/coralblocks/CoralProto/blob/main/src/main/java/com/coralblocks/coralproto/enums/IntEnum.java) or [TwoCharEnum](https://github.com/coralblocks/CoralProto/blob/main/src/main/java/com/coralblocks/coralproto/enums/TwoCharEnum.java). Below an example:
```java
public static enum Side implements CharEnum { 

    BUY('B'), 
    SELL('S');

    private final char b;
    public final static CharMap<Side> ALL = new CharMap<Side>();
    
    static {
        for(Side s : Side.values()) {
            if (ALL.put(s.getChar(), s) != null) {
                throw new IllegalStateException("Cannot have two sides with the same character: " + s);
            }
        }
    }
    
    private Side(char b) {
        this.b = b;
    }
    
    @Override
    public final char getChar() {
        return b;
    }
}
```
And to define in your schema you simply do:
```plain
    side:     charEnum(Side) 
```
The corresponding char of the enum will be transmitted through the wire.

## Float and Double Fields
- Floats are transmitted through the wire as integers (4-byte big-endian). The default precision is 4 decimals.
- Doubles are transmitted through the wire as longs (8-byte big integer). The default precision is 8 decimals.

If you need more or less decimal precision, you can pass the number of decimals when defining the field in the schema:
```plain
    myFloat1: float 
    myFloat2: float(3)
    myFloat3: float(5)
    myDouble1: double 
    myDouble2: double(7)
    myDouble3: double(9)	
```

## Evolving the Schema
You can evolve the schema without breaking compatibility by appending new fields to the end of your message. For example, you can evolve:
```plain
    CLASSNAME = com.coralblocks.coralproto.example.ProtoMessage1
    TYPE = P
    SUBTYPE = A
    
    symbolId: long
    symbolDesc: varchars(128)!
```
by appending a new field:
```plain
    CLASSNAME = com.coralblocks.coralproto.example.ProtoMessage1A
    TYPE = P
    SUBTYPE = A
    
    symbolId: long
    symbolDesc: varchars(128)!
    extraField: int
```
Then you can send an old version (without the field) to the new version:
```java
ByteBuffer bb = ByteBuffer.allocate(1024);

ProtoMessage1 p1 = new ProtoMessage1();

p1.symbolId.set(2L);
p1.symbolDesc.set("IBM");

p1.write(bb);

bb.flip();

Assert.assertEquals(ProtoMessage1A.TYPE, bb.get());
Assert.assertEquals(ProtoMessage1A.SUBTYPE, bb.get());

// schema has evolved, it now has an extra field...

ProtoMessage1A p1A = new ProtoMessage1A();

p1A.read(bb);

Assert.assertEquals(2L, p1A.symbolId.get());
Assert.assertEquals("IBM", p1A.symbolDesc.get().toString());
Assert.assertEquals(0, p1A.extraField.get()); // default value
```
And you can send a new version (with the field) to the old version:
```java
ByteBuffer bb = ByteBuffer.allocate(1024);

ProtoMessage1A p1A = new ProtoMessage1A();

p1A.symbolId.set(2L);
p1A.symbolDesc.set("IBM");
p1A.extraField.set(111);

p1A.write(bb);

bb.flip();

Assert.assertEquals(ProtoMessage1.TYPE, bb.get());
Assert.assertEquals(ProtoMessage1.SUBTYPE, bb.get());

ProtoMessage1 p1 = new ProtoMessage1();

p1.read(bb);

Assert.assertEquals(2L, p1.symbolId.get());
Assert.assertEquals("IBM", p1.symbolDesc.get().toString());
```
**NOTE:** We are changing the message name from `ProtoMessage1` to `ProtoMessage1A` just to be able to test inside the same class. When you evolve a message you will want to keep the same message name.

## Generating Source Code
To generate the Java source code of your messages from the schema definition files, you should do:
```plain
$ java com.coralblocks.coralproto.IDL <FOLDER_NAME> <DRY_RUN> <EXTENSION>
```
- The `FOLDER_NAME` argument is the folder where the test files containing the scheme definition of your messages are located. Each message should have its own file.
  
- The `DRY_RUN` argument is to test without replacing any source code. It defaults to false.
  
- The `EXTENSION` argument is the extension of the text files with the schema definition. It defaults to `.idl`.
  
The source code of the messages will be generated inside the same folder.

**NOTE:** When the source code is generated you will most probably need to use `ORGANIZE IMPORTS` (usually CTRL + O) of your IDE to add the correct import statements for the code to compile.

## Logging in Ascii
You can print/log your message in ascii. See below:
```java
bb.clear();
proto.writeAscii(true, bb); // short version (without the message name, just type and subtype)
bb.flip();

Assert.assertEquals("AF|Y|33|S|1111|222222|3300", ByteBufferUtils.parseString(bb));

bb.clear();
received.writeAscii(false, bb); // long version (with the message name, type and subtype)
bb.flip();

Assert.assertEquals("AF (AllFieldsProtoMessage)|Y|33|S|1111|222222|3300", ByteBufferUtils.parseString(bb));
```
