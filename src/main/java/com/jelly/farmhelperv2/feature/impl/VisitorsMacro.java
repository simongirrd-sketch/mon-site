package com.jelly.farmhelperv2.feature.impl;

import com.jelly.farmhelperv2.feature.IFeature;
import com.jelly.farmhelperv2.handler.GameStateHandler;
import com.jelly.farmhelperv2.handler.MacroHandler;
import com.jelly.farmhelperv2.util.InventoryUtils;
import com.jelly.farmhelperv2.util.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Automatically handles Garden visitors by fulfilling their crop requests.
 * Converted from Forge 1.8.9 to Fabric 1.21.1
 */
public class VisitorsMacro implements IFeature {

    private static VisitorsMacro instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean running = false;
    private int tickDelay = 0;

    private enum State { SEARCHING, INTERACTING, FULFILLING, DONE }
    private State state = State.SEARCHING;

    public static VisitorsMacro getInstance() {
        if (instance == null) instance = new VisitorsMacro();
        return instance;
    }

    @Override
    public String getName() { return "VisitorsMacro"; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public void start() {
        if (!GameStateHandler.getInstance().isInGarden()) {
            LogUtils.sendWarning("[VisitorsMacro] Not in Garden!");
            return;
        }
        running = true;
        state = State.SEARCHING;
        tickDelay = 0;
        MacroHandler.getInstance().pauseMacro();
        LogUtils.sendMessage("§a[VisitorsMacro] §fStarted. Looking for visitors...");
    }

    @Override
    public void stop() {
        running = false;
        state = State.SEARCHING;
        MacroHandler.getInstance().resumeMacro();
        LogUtils.sendMessage("§a[VisitorsMacro] §fStopped.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (tickDelay > 0) { tickDelay--; return; }

        switch (state) {
            case SEARCHING -> searchForVisitor();
            case INTERACTING -> handleVisitorGui();
            case FULFILLING -> fulfillRequest();
            case DONE -> stop();
        }
    }

    private void searchForVisitor() {
        // Visitors are NPC entities with specific names
        Box searchBox = mc.player.getBoundingBox().expand(30);
        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox,
                e -> e instanceof PlayerEntity && isVisitor(e));

        if (!entities.isEmpty()) {
            Entity visitor = entities.get(0);
            LogUtils.sendMessage("§a[VisitorsMacro] §fFound visitor: §e" + visitor.getName().getString());

            // Interact with visitor
            mc.interactionManager.interactEntity(mc.player, visitor, net.minecraft.util.Hand.MAIN_HAND);
            state = State.INTERACTING;
            tickDelay = 10;
        }
    }

    private boolean isVisitor(Entity entity) {
        String name = entity.getName().getString();
        // Visitors have known names in Skyblock
        return name.contains("Visitor") || name.contains("Jacob") ||
                name.contains("Trevor") || name.contains("Anita");
    }

    private void handleVisitorGui() {
        if (InventoryUtils.isContainerOpen("Visitor")) {
            int acceptSlot = InventoryUtils.findContainerSlotByName("Accept Offer");
            if (acceptSlot != -1) {
                mc.interactionManager.clickSlot(
                        mc.player.currentScreenHandler.syncId,
                        acceptSlot, 0, net.minecraft.screen.slot.SlotActionType.PICKUP,
                        mc.player
                );
                state = State.DONE;
                tickDelay = 20;
                LogUtils.sendMessage("§a[VisitorsMacro] §fAccepted visitor offer!");
            }
        }
    }

    private void fulfillRequest() {
        // This would involve checking what items the visitor needs
        // and placing them in the offer slots
        state = State.DONE;
    }

    @Override
    public void onWorldLoad() { stop(); }
}
