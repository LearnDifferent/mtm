package com.github.learndifferent.mtm.controller;


import com.github.learndifferent.mtm.service.BatchJobService;
import com.github.learndifferent.mtm.vo.BatchJobVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Batch Job Controller
 *
 * @author zhou
 * @date 2024/2/23
 */
@RequestMapping("/batch-job")
@RestController
@RequiredArgsConstructor
public class BatchJobController {

    private final BatchJobService batchJobService;

    @GetMapping("/update-bookmark-view")
    public BatchJobVO updateBookmarkView() {
        return batchJobService.updateBookmarkView();
    }
}