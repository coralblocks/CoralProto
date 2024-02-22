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
