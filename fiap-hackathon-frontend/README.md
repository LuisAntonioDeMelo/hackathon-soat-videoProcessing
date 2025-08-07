# FIAP Hackathon - Frontend

Frontend React/Next.js para visualização de vídeos processados e frames extraídos do projeto FIAP Hackathon.

## Funcionalidades

- 📁 **Upload de Vídeos**: Interface drag-and-drop para envio de arquivos de vídeo
- ⏳ **Status em Tempo Real**: Acompanhamento do processamento via polling
- 🖼️ **Visualização de Frames**: Exibição individual ou em grade dos frames extraídos
- 📥 **Download**: Download individual de frames ou ZIP completo
- 🎯 **Interface Responsiva**: Funciona em desktop e mobile

## Tecnologias

- **Next.js 14** - Framework React
- **TypeScript** - Tipagem estática
- **Tailwind CSS** - Estilização
- **Lucide React** - Ícones

## Instalação

1. **Instalar dependências**:
```bash
npm install
```

2. **Executar em desenvolvimento**:
```bash
npm run dev
```

3. **Acessar aplicação**:
- Frontend: http://localhost:3000
- Backend (deve estar rodando): http://localhost:8080

## Configuração

### Backend Integration

O frontend se conecta automaticamente ao backend através de:

- **Proxy configurado** no `next.config.js` que redireciona `/api/*` para `http://localhost:8080/api/*`
- **Endpoints utilizados**:
  - `POST /api/videos/upload` - Upload de vídeos
  - `GET /api/videos/status/{id}` - Status do processamento
  - `GET /api/videos/download/{id}` - Download do ZIP
  - `GET /api/frames/{videoId}/list` - Lista de frames
  - `GET /api/frames/{videoId}/{filename}` - Frame individual

### Estrutura de Pastas

```
fiap-hackathon-frontend/
├── app/
│   ├── components/
│   │   ├── VideoUpload.tsx      # Upload de vídeos
│   │   ├── VideoStatus.tsx      # Status do processamento
│   │   └── FrameViewer.tsx      # Visualizador de frames
│   ├── globals.css              # Estilos globais
│   ├── layout.tsx               # Layout raiz
│   └── page.tsx                 # Página principal
├── next.config.js               # Configuração do Next.js
└── package.json                 # Dependências
```

## Como Usar

1. **Upload de Vídeo**:
   - Arraste um arquivo de vídeo ou clique para selecionar
   - Formatos suportados: MP4, WebM, AVI, MOV (máx. 100MB)
   - Clique em "Processar Vídeo"

2. **Acompanhar Processamento**:
   - O status é atualizado automaticamente a cada 2 segundos
   - Estados: PROCESSING → COMPLETED/ERROR

3. **Visualizar Frames**:
   - **Modo Individual**: Navegue frame por frame com controles
   - **Modo Grade**: Visualize todos os frames em miniatura
   - **Fullscreen**: Clique na imagem para visualização expandida

4. **Downloads**:
   - **ZIP Completo**: Botão "Download ZIP" quando processamento concluído
   - **Frame Individual**: Botão de download em cada frame

## Integração com Backend

Para que o frontend funcione corretamente, certifique-se de que:

1. **Backend está rodando** na porta 8080
2. **FrameController foi adicionado** ao backend (arquivo fornecido)
3. **Estrutura de pastas** no backend:
   ```
   uploads/
   └── {videoId}/
       └── frames/
           ├── frame_001.jpg
           ├── frame_002.jpg
           └── ...
   ```

## Desenvolvimento

### Adicionar novos componentes:
```bash
# Criar novo componente
touch app/components/NovoComponente.tsx
```

### Build para produção:
```bash
npm run build
npm start
```

### Linting:
```bash
npm run lint
```

## Estrutura do Componente de Frames

O `FrameViewer` é inteligente e:

1. **Busca lista real** de frames via API `/api/frames/{videoId}/list`
2. **Fallback automático** para nomes sequenciais se API falhar
3. **Cache de imagens** com controle de erro e placeholders
4. **Performance otimizada** com lazy loading implícito

## Próximos Passos

- [ ] Adicionar preview do vídeo original
- [ ] Implementar filtros de frames
- [ ] Adicionar comparação lado a lado
- [ ] Otimizar carregamento com lazy loading
- [ ] Adicionar modo cinema (slideshow)

## Troubleshooting

### Frames não aparecem:
1. Verifique se backend está rodando
2. Confirme se FrameController foi adicionado
3. Verifique estrutura de pastas no diretório uploads

### Upload falha:
1. Confirme tamanho do arquivo (máx. 100MB)
2. Verifique formato de vídeo suportado
3. Confirme conectividade com backend

### Status não atualiza:
1. Verifique console do browser por erros
2. Confirme se endpoint `/api/videos/status/{id}` funciona
3. Verifique se processamento está ativo no backend
