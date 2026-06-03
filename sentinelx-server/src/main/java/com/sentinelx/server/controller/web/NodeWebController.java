package com.sentinelx.server.controller.web;

import com.sentinelx.server.domain.entity.Node;
import com.sentinelx.server.service.NodeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nodes")
public class NodeWebController {

    private final NodeService nodeService;

    public NodeWebController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("nodes", nodeService.findAll());
        return "nodes/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newNodeForm(Model model) {
        model.addAttribute("node", new Node());
        model.addAttribute("mode", "create");
        return "nodes/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createNode(@ModelAttribute("node") Node node,
                             RedirectAttributes redirectAttributes) {
        nodeService.create(node);
        redirectAttributes.addFlashAttribute("successMessage", "Node created successfully.");
        return "redirect:/nodes";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Node node = nodeService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
        model.addAttribute("node", node);
        model.addAttribute("mode", "edit");
        return "nodes/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateNode(@PathVariable("id") Long id,
                             @ModelAttribute("node") Node node,
                             RedirectAttributes redirectAttributes) {
        nodeService.update(id, node);
        redirectAttributes.addFlashAttribute("successMessage", "Node updated successfully.");
        return "redirect:/nodes";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNode(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        nodeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Node deleted.");
        return "redirect:/nodes";
    }

    @GetMapping("/{id}")
    public String nodeDetail(@PathVariable("id") Long id, Model model) {
        Node node = nodeService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
        model.addAttribute("node", node);
        return "nodes/detail";
    }

    @PostMapping("/{id}/regenerate-token")
    @PreAuthorize("hasRole('ADMIN')")
    public String regenerateToken(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Node node = nodeService.regenerateToken(id);
        redirectAttributes.addFlashAttribute("successMessage", "Token regenerated: " + node.getAgentToken());
        return "redirect:/nodes/" + id;
    }
}
