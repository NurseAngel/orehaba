package mods.nurseangel.orehaba;

import mods.nurseangel.orehaba.handler.HandlerController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class OrehabaCrossBlock extends Block {

	/**
	 * コンストラクタ
	 *
	 * @param par1
	 * @param par2Material
	 */

	protected OrehabaCrossBlock(int par1) {
		super(par1, Material.ground);
		this.setCreativeTab(CreativeTabs.tabBlock);
		setStepSound(soundGravelFootstep);
	}

	/**
	 * ブロックの色乗算 手持ち時
	 *
	 * @param int メタデータ
	 */
	@Override
	public int getRenderColor(int metadata) {
		return 0x606060;
	}

	/**
	 * ブロックの色乗算 設置時
	 *
	 * @param IBlockAccess
	 * @param int x,y,z
	 */
	@Override
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		return 0x606060;
	}

	/**
	 * 世界に配置された
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		// プレイヤー以外に置かれた場合はパス(エンダーとか?)
		if (!(par5EntityLivingBase instanceof EntityPlayer)) {
			return;
		}

		// 方向を算出
		int direction = MathHelper.floor_double((double) ((par5EntityLivingBase.rotationYaw * 4F) / 360F) + 2.5D) & 3;

		// 配置
		HandlerController.getInstance().onBlockPlacedByPlayer(par1World, par2, par3, par4, direction);

	}

}
