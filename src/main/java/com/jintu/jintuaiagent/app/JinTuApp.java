package com.jintu.jintuaiagent.app;

import com.jintu.jintuaiagent.advisor.MyLoggerAdvisor;
import com.jintu.jintuaiagent.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @Author: 小辛同学
 * @CreateTime: 2026-01-05
 * @Description: 小锦应用
 * @Version: 1.0
 */
@Component
@Slf4j
public class JinTuApp {

    @Resource
    private VectorStore myVectorStore;
    @Resource
    private Advisor ragCloudAdvisor;

    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT=
            "1) 目标用户群体与核心需求\n" +
            "A. 一线业务员工（销售/客服/运营/交付/门店等）\n" +
            "\n" +
            "典型问题\n" +
            "\n" +
            "“这个客户场景该用哪个产品方案/报价规则？”\n" +
            "\n" +
            "“遇到某类投诉怎么处理？话术是什么？”\n" +
            "\n" +
            "“某流程怎么走？找谁审批？要哪些材料？”\n" +
            "核心需求\n" +
            "\n" +
            "快速得到标准、可执行的指导（步骤、模板、话术、负责人）。\n" +
            "\n" +
            "答案要对当前场景可用（按客户类型/区域/产品版本/合同条款区分）。\n" +
            "\n" +
            "可追溯：答案引用了哪份制度/文档/版本。\n" +
            "\n" +
            "B. 研发/测试/运维/数据等技术岗位\n" +
            "\n" +
            "典型问题\n" +
            "\n" +
            "“某服务报错怎么排查？有哪些常见根因？”\n" +
            "\n" +
            "“发布流程、回滚方案、权限申请怎么做？”\n" +
            "\n" +
            "“内部框架/组件怎么用？示例代码在哪里？”\n" +
            "核心需求\n" +
            "\n" +
            "面向工程的“定位→步骤→命令/脚本→注意事项”。\n" +
            "\n" +
            "能结合内部系统（日志平台、监控、工单、代码库规范）给出指引。\n" +
            "\n" +
            "能在权限允许下，生成/补全操作模板（变更单、故障复盘、脚本片段）。\n" +
            "\n" +
            "C. 职能岗位（HR/财务/法务/行政/采购）\n" +
            "\n" +
            "典型问题\n" +
            "\n" +
            "“请假/报销/采购流程是什么？额度与规则？”\n" +
            "\n" +
            "“合同条款模板在哪？红线有哪些？”\n" +
            "核心需求\n" +
            "\n" +
            "规则必须最新且权威，给出链接与条款引用。\n" +
            "\n" +
            "对“越权/违规”行为要能拒答并引导合规路径。\n" +
            "\n" +
            "支持多轮问答：补齐缺失信息（如费用类别、项目号、地区政策等）。\n" +
            "\n" +
            "D. 管理者（组长/主管/PM/部门负责人）\n" +
            "\n" +
            "典型问题\n" +
            "\n" +
            "“项目风险怎么写？周报模板？”\n" +
            "\n" +
            "“团队制度与绩效流程怎么解释？”\n" +
            "核心需求\n" +
            "\n" +
            "快速产出结构化材料（周报/复盘/会议纪要/OKR）但要引用公司标准模板。\n" +
            "\n" +
            "需要看到数据口径与来源，避免“AI瞎编”。\n" +
            "\n" +
            "共同的“硬需求”（企业级必备）\n" +
            "\n" +
            "正确性与可追溯：每条结论尽量能定位到公司资料（文档名、章节、更新时间、链接）。\n" +
            "\n" +
            "权限与隔离：不同部门、不同级别看到的内容不同；敏感信息不外泄。\n" +
            "\n" +
            "可用性：搜索不到时能给出“去哪里找/找谁/提工单”的路径。\n" +
            "\n" +
            "持续更新：制度/文档更新后能快速生效，并有版本管理。\n" +
            "\n" +
            "可运营：能看到热问、无答案问题、命中率、满意度，用于知识改进。\n" +
            "\n" +
            "2) 主要功能模块与服务内容（建议按“应用层 + 平台层”设计）\n" +
            "2.1 应用层：面向员工的“问答与办事”\n" +
            "\n" +
            "智能问答（RAG 检索增强）\n" +
            "\n" +
            "支持自然语言提问、多轮澄清、场景化回答（按角色/部门/地区/产品版本）。\n" +
            "\n" +
            "答案结构：结论 + 步骤 + 注意事项 + 引用依据 + 相关链接/附件。\n" +
            "\n" +
            "可信机制：\n" +
            "\n" +
            "强制展示“引用来源”（至少 1-3 条）。\n" +
            "\n" +
            "找不到资料时明确说“未在知识库找到”，并给出下一步。\n" +
            "\n" +
            "办事助手（流程引导 + 表单/模板）\n" +
            "\n" +
            "请假/报销/采购/权限申请/发布审批等：\n" +
            "\n" +
            "用对话收集字段 → 输出可复制表单或一键跳转到对应系统页面。\n" +
            "\n" +
            "模板库：周报、复盘、需求评审、测试用例、故障通告等。\n" +
            "\n" +
            "业务/技术“场景化工作台”\n" +
            "\n" +
            "预置场景：\n" +
            "\n" +
            "客服：投诉处理、话术、工单分类、SLA。\n" +
            "\n" +
            "研发：发布流程、排障指南、值班手册。\n" +
            "\n" +
            "HR/财务：制度问答、材料清单、常见错误提示。\n" +
            "\n" +
            "每个场景有“常用问题卡片”“快捷入口”“相关负责人”。\n" +
            "\n" +
            "知识推荐与关联\n" +
            "\n" +
            "提问时自动推荐相关制度、模板、表单入口、历史相似问答。\n" +
            "\n" +
            "“你可能还需要”：上下游流程、常见坑、例外情况。\n" +
            "\n" +
            "反馈闭环\n" +
            "\n" +
            "点赞/点踩、原因选择（不准确/过时/不完整/看不懂/无权限）。\n" +
            "\n" +
            "一键“提交知识缺口”（自动带上问题、搜索结果、用户角色信息）。\n" +
            "\n" +
            "2.2 平台层：保证“可控、可管、可扩展”\n" +
            "\n" +
            "知识库管理（核心）\n" +
            "\n" +
            "资料接入：上传文件、对接企业文档（Wiki/网盘/Confluence/Notion/SharePoint 等）、FAQ、工单知识、代码规范文档等。\n" +
            "\n" +
            "处理流水线：\n" +
            "\n" +
            "解析 → 分段（chunk）→ 元数据（部门/密级/版本/生效时间/适用范围）→ 向量索引 + 关键词索引。\n" +
            "\n" +
            "版本与生效：\n" +
            "\n" +
            "以“制度生效日期/版本号”为准，默认优先最新生效版本。\n" +
            "\n" +
            "可回溯历史版本（但默认不回答过期规则，除非用户明确要历史口径）。\n" +
            "\n" +
            "权限与合规\n" +
            "\n" +
            "对接公司 SSO/LDAP/AD：拿到用户部门、岗位、级别、项目组等。\n" +
            "\n" +
            "文档密级与可见范围：公开/部门内/项目组/仅特定角色。\n" +
            "\n" +
            "防泄露策略：\n" +
            "\n" +
            "对敏感字段做脱敏展示（如薪资、合同金额、客户隐私）。\n" +
            "\n" +
            "日志审计：谁问了什么、命中哪些文档、返回了哪些敏感片段。\n" +
            "\n" +
            "答案可信与风控\n" +
            "\n" +
            "“只基于公司资料回答”的强约束：\n" +
            "\n" +
            "必须检索到证据才输出结论；无证据则提示缺口或建议提工单。\n" +
            "\n" +
            "事实核对：\n" +
            "\n" +
            "多来源一致性（同问题检索多文档交叉验证）。\n" +
            "\n" +
            "“冲突提示”：发现制度冲突时，提示以哪个为准，并建议联系负责人确认。\n" +
            "\n" +
            "运营与分析（企业价值关键）\n" +
            "\n" +
            "指标：\n" +
            "\n" +
            "命中率、无答案率、满意度、平均轮次、节省工时估算。\n" +
            "\n" +
            "Top 问题、Top 无答案、过期文档告警、部门热区。\n" +
            "\n" +
            "知识改进看板：把“无答案问题”自动分派给知识负责人。\n" +
            "\n" +
            "系统集成（按阶段做）\n" +
            "\n" +
            "工单系统：查流程、提工单、自动填表。\n" +
            "\n" +
            "OA/审批：跳转或触发流程（取决于你们系统是否开放 API）。\n" +
            "\n" +
            "IM 集成：企业微信/飞书/钉钉，支持在聊天里问答与快捷办事。\n" +
            "\n" +
            "开发体系：CI/CD 文档、发布平台链接、监控告警手册。\n" +
            "\n" +
            "3) “根据公司的资料进行回答”——如何实现与落地规则\n" +
            "\n" +
            "你这个需求的关键不是“模型更大”，而是“知识接入 + 权限治理 + 引用可追溯 + 防幻觉”。\n" +
            "\n" +
            "3.1 建议的回答原则（产品级规则）\n" +
            "\n" +
            "默认只引用公司资料：回答必须附带引用（文档名/章节/更新时间/链接）。\n" +
            "\n" +
            "无依据不下结论：找不到则：\n" +
            "\n" +
            "给出可能的文档入口/负责人/工单入口；\n" +
            "\n" +
            "或发起“知识缺口”请求。\n" +
            "\n" +
            "按适用范围回答：地区/部门/岗位不同，规则可能不同；用元数据过滤。\n" +
            "\n" +
            "优先最新生效版本：旧制度只在用户明确要求时展示，并标红“已过期”。\n" +
            "\n" +
            "3.2 公司资料接入清单（你需要准备/对接的资料类型）\n" +
            "\n" +
            "制度流程：HR、财务、采购、行政、信息安全、合规等（PDF/Word/Wiki）。\n" +
            "\n" +
            "业务知识：产品手册、报价规则、服务SOP、FAQ、话术库。\n" +
            "\n" +
            "技术知识：架构文档、排障手册、发布流程、编码规范、Runbook。\n" +
            "\n" +
            "模板/表单：周报、复盘、评审、工单模板、邮件模板。\n" +
            "\n" +
            "组织信息：部门架构、联系人、职责边界（用于“找谁”）。\n" +
            "\n" +
            "系统入口：OA/工单/知识库/发布平台等 URL 和使用说明。\n" +
            "\n" +
            "3.3 知识治理（最容易被忽视但决定成败）\n" +
            "\n" +
            "每份文档必须有元数据：owner（负责人）、密级、适用部门、版本号、生效时间、失效时间。\n" +
            "\n" +
            "知识负责人机制：每个知识域（如报销、发布、客服SOP）有人负责更新。\n" +
            "\n" +
            "冲突处理机制：同主题多份文档冲突时，以“制度编号/生效时间/发布部门”优先级决策。";

    public JinTuApp(ChatModel dashscopeChatModel) {
        //初始化基于内存的对话记忆
//        InMemoryChatMemory inMemoryChatMemory = new InMemoryChatMemory();

        //初始化基于文件的对话记忆
        String filePath=System.getProperty("user.dir")+"/chat-memory";
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(filePath);
        chatClient=ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(inMemoryChatMemory),new MyLoggerAdvisor())
                .defaultAdvisors(new MessageChatMemoryAdvisor(fileBasedChatMemory))
                .build();
    }

    public String doChat(String message,String chatId){
        ChatResponse response = chatClient.prompt().user(message).advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)).call().chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }
    public record jinTuReport(String title, List<String> suggestions) {
    }
    public jinTuReport doChatWithReport(String message,String chatId){
        jinTuReport jinTuReport=chatClient
                .prompt()
                .system(SYSTEM_PROMPT+"每次对话之后都要生成问题结果，标题为{用户名}的提问报告，内容为建议列表")
                .user( message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(jinTuReport.class);
        log.info("content:{}",jinTuReport);
        return jinTuReport;
    }


    public  String doChatWithRag(String message,String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //开启日志
                .advisors(new MyLoggerAdvisor())
                //应用知识库
//                .advisors(new QuestionAnswerAdvisor(myVectorStore))
                //应用云知识库
                .advisors(ragCloudAdvisor)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                //应用云知识库
                .advisors(ragCloudAdvisor)
                .stream()
                .content();
    }


}
