# MouseDelayFix

MouseDelayFix is a forge mod for Minecraft 1.8.x, 1.9.x and 1.10.x to fix the mouse delay bug introduced in Minecraft 1.8 (https://bugs.mojang.com/browse/MC-67665).

***

### The bug

```java
// EntityLivingBase
public Vec3 getLook(float partialTicks) {
    if (partialTicks == 1.0F) {
        return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
    } else {
        float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
        float f1 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
        return this.getVectorForRotation(f, f1);
    }
}
```

Since 1.8 entity look direction is being derived from `rotationYawHead` instead of `rotationYaw`. This is bad for players because the angle your head faces is not the same as your crosshair location.

***

### Mojang's fix (MC 1.11)

```java
// EntityPlayerSP
public Vec3d getLook(float partialTicks) {
    return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
}
```

The fix added in 1.11 is to override `getLook` in the client player class (`EntityPlayerSP`) and use `rotationYaw`.

***

### My fix

```diff
 // EntityLivingBase
 public Vec3 getLook(float partialTicks) {
+    if (this instanceof EntityPlayerSP) {
+        return super.getLook(f);
+    }
     if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
     } else {
         float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
         float f1 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
         return this.getVectorForRotation(f, f1);
     }
 }
```

My fix injects the above code into `EntityLivingBase.getLook`. This works because `Entity.getLook` uses `rotationYaw` (non-living entities don't have heads!). This is equivalent to Mojang's 1.11 fix.
