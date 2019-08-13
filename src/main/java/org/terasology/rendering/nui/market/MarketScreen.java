/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.market;

import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.market.MarketClientSystem;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.layouts.FlowLayout;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.world.block.Block;

import java.util.Collections;
import java.util.Set;

public class MarketScreen extends CoreScreenLayer {

    private FlowLayout marketItemList;
    private UILabel itemName;
    private ItemIcon itemDisplayIcon;
    private UILabel itemDescription;
    private Prefab selectedPrefab;
    private Block selectedBlock;

    @In
    MarketClientSystem marketManager;

    @Override
    public void initialise() {
        marketItemList = find("marketItemList", FlowLayout.class);
        itemName = find("itemName", UILabel.class);
        itemDisplayIcon = find("itemDisplayIcon", ItemIcon.class);
        itemDescription = find("itemDescription", UILabel.class);
    }

    @Override
    public void onOpened() {
        populateMarketItems(marketManager.getPurchasablePrefabs(), marketManager.getPurchasableBlocks());
    }

    @Override
    public void onClosed() {
        marketItemList.removeAllWidgets();
        itemName.setText("");
        itemDisplayIcon.setIcon(null);
        itemDisplayIcon.setMesh(null);
        itemDescription.setText("");
    }

    private void populateMarketItems(Set<Prefab> purchasablePrefabs, Set<Block> purchasableBlocks) {
        for (Prefab prefab : purchasablePrefabs) {
            ItemComponent itemComponent = prefab.getComponent(ItemComponent.class);
            ItemIcon itemIcon = new ItemIcon();
            itemIcon.setIcon(itemComponent.icon);

            UIInteractionWrapper wrapper = new UIInteractionWrapper();
            wrapper.setTooltipLines(Collections.singletonList(new TooltipLine(getPrefabName(prefab))));
            wrapper.setListener(widget -> onPrefabSelected(prefab));
            wrapper.setContent(itemIcon);
            marketItemList.addWidget(wrapper, null);
        }
        for (Block block : purchasableBlocks) {
            ItemIcon itemIcon = new ItemIcon();
            itemIcon.setMesh(block.getMeshGenerator().getStandaloneMesh());

            UIInteractionWrapper wrapper = new UIInteractionWrapper();
            wrapper.setTooltipLines(Collections.singletonList(new TooltipLine(getBlockName(block))));
            wrapper.setListener(widget -> onBlockSelected(block));
            wrapper.setContent(itemIcon);
            marketItemList.addWidget(wrapper, null);
        }
    }

    private void onPrefabSelected(Prefab prefab) {
        selectedBlock = null;
        selectedPrefab = prefab;
        itemName.setText(getPrefabName(prefab));
        itemDisplayIcon.setMesh(null);
        itemDisplayIcon.setIcon(prefab.getComponent(ItemComponent.class).icon);
    }

    private void onBlockSelected(Block block) {
        if (block.getPrefab().isPresent()) {
            onPrefabSelected(block.getPrefab().get());
            return;
        }
        selectedPrefab = null;
        selectedBlock = block;
        itemName.setText(getBlockName(block));
        itemDisplayIcon.setIcon(null);
        itemDisplayIcon.setMesh(block.getMeshGenerator().getStandaloneMesh());
    }

    private String getPrefabName(Prefab prefab) {
        return prefab.hasComponent(DisplayNameComponent.class)
                ? prefab.getComponent(DisplayNameComponent.class).name
                : prefab.getUrn().getResourceName().toString();
    }

    private String getBlockName(Block block) {
        String displayName = block.getDisplayName();
        return !displayName.equals("Untitled Block")
                ? displayName
                : block.getURI()
                .getBlockFamilyDefinitionUrn()
                .getResourceName()
                .toString();
    }
}
