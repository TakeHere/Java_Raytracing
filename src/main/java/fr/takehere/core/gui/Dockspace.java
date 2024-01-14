package fr.takehere.core.gui;
import fr.takehere.core.Consts;
import fr.takehere.core.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class Dockspace {

    public static void setupDockspace(){
        if(Consts.SHOW_GUI){
            int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

            ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
            ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                    ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                    ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoMouseInputs;

            ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
            ImGui.popStyleVar(2);

            // Dockspace
            ImGui.dockSpace(ImGui.getID("Dockspace"));
        }
    }
}
