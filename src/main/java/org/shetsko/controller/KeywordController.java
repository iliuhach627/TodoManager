package org.shetsko.controller;

import lombok.RequiredArgsConstructor;
import org.shetsko.service.KeywordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {
    private final KeywordService keywordService;

    @GetMapping
    public String manageKeywords(Model model) {
        model.addAttribute("keywords", keywordService.getAllKeywords());
        model.addAttribute("keywordCount", keywordService.getKeywordCount());
        model.addAttribute("newKeyword", "");
        return "tasks/manage";
    }

    @PostMapping("/add")
    public String addKeyword(@RequestParam String newKeyword,
                             RedirectAttributes redirectAttributes) {
        boolean success = keywordService.addKeyword(newKeyword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ключевое слово '" + newKeyword + "' успешно добавлено!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Не удалось добавить ключевое слово '" + newKeyword + "'");
        }
        return "redirect:/keywords";
    }

    @PostMapping("/delete")
    public String deleteKeyword(@RequestParam String keyword,
                                RedirectAttributes redirectAttributes) {
        boolean success = keywordService.removeKeyword(keyword);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ключевое слово '" + keyword + "' успешно удалено!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Не удалось удалить ключевое слово '" + keyword + "'");
        }
        return "redirect:/keywords";
    }

    @PostMapping("/reload")
    public String reloadKeywords(RedirectAttributes redirectAttributes) {
        keywordService.reloadKeywords();
        redirectAttributes.addFlashAttribute("infoMessage",
                "Ключевые слова перезагружены из базы данных");
        return "redirect:/keywords";
    }
}