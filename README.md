# 🎨 ContentsLab (콘텐츠랩)
> **SNS 크리에이터를 위한 AI 기반 콘텐츠 기획 & 인스타툰 자동 생성 SaaS**

![ContentsLab Banner](https://capsule-render.vercel.app/api?type=waving&color=00A896&customBgColor=A8E6CF&height=200&section=header&text=Contents%20Lab&fontSize=60&fontColor=ffffff)

<div align="center">

[![Tech Stack](https://img.shields.io/badge/Java-17-orange?logo=java)]()
[![Framework](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)]()
[![Python](https://img.shields.io/badge/Python-3.10-blue?logo=python)]()
[![AI Integration](https://img.shields.io/badge/HuggingFace-API-yellow?logo=huggingface)]()
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()

</div>

---

## 📌 1. 프로젝트 소개
**ContentsLab**은 인스타툰 작가, 릴스/숏폼 기획자 등 SNS 크리에이터의 아이디에이션부터 최종 이미지 제작까지의 공정을 단축해주는 **AI 파이프라인 플랫폼**입니다. 

* 🤖 **페르소나/대본/이미지 3종 AI 챗봇**을 제공하여 기획 및 제작 작업의 생산성을 최적화합니다.
* 💳 **결제 API 연동 및 구독 서비스**를 제공하는 커머셜 라이브 SaaS입니다.

---

## ✨ 2. 핵심 기능 (Key Features)

| 1. 캐릭터 페르소나 챗봇 | 2. 콘텐츠 기획/대본 챗봇 |
| :---: | :---: |
| 크리에이터 콘셉트에 맞춘 캐릭터 성격, 말투, 세계관 설정 | 플랫폼 최적화 스크립트, 훅(Hooking) 문구 자동 추출 |

| 3. 인스타툰 자동 생성 챗봇 | 4. 구독 & 결제 시스템 |
| :---: | :---: |
| 설정한 캐릭터 + 대사를 결합한 컷 툰 시각화 생성 | PG 결제 연동, 플랜별 권한 제어 및 결제 내역 관리 |

---

## 🏗️ 3. 시스템 아키텍처 (Architecture)

```mermaid
graph TD
    User([User / Creator]) <--> Frontend[Web Dashboard]
    Frontend <--> Backend[Spring Boot Server]
    
    subgraph "AI Engine (Python/HuggingFace)"
        Backend <-->|REST API| AICore[AI Agent Controller]
        AICore -->|Prompt Chain| HF1[Character Persona Bot]
        AICore -->|Script Generator| HF2[Content Planning Bot]
        AICore -->|Multimodal Image Gen| HF3[Instatoon Bot]
    end
    
    subgraph "Database & Payment"
        Backend <--> DB[(MySQL / DB)]
        Backend <-->|Payment API| PG[Portone / Payment Gateway]
    end
