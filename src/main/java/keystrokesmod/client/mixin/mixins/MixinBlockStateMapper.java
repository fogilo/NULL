package keystrokesmod.client.mixin.mixins;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStateMapper.class)
public class MixinBlockStateMapper {

    @Redirect(method = "putAllStateModelLocations", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Objects;firstNonNull(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private <T> T redirectFirstNonNull(T first, T second) {
        return first != null ? first : second;
    }
}
