'use client'

import { useState, useRef } from 'react'
import { Upload, Video, AlertCircle } from 'lucide-react'

interface VideoResponse {
  id: string;
  message: string;
  videoName: string;
  videoSize: string;
  tempoDeProcessamento: string;
}

interface VideoUploadProps {
  onVideoUploaded: (videoData: VideoResponse) => void;
}

export default function VideoUpload({ onVideoUploaded }: VideoUploadProps) {
  const [isDragging, setIsDragging] = useState(false)
  const [isUploading, setIsUploading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(true)
  }

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
    
    const files = Array.from(e.dataTransfer.files)
    const videoFile = files.find(file => file.type.startsWith('video/'))
    
    if (videoFile) {
      setSelectedFile(videoFile)
      setError(null)
    } else {
      setError('Por favor, selecione um arquivo de vídeo válido.')
    }
  }

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file && file.type.startsWith('video/')) {
      setSelectedFile(file)
      setError(null)
    } else {
      setError('Por favor, selecione um arquivo de vídeo válido.')
    }
  }

  const handleUpload = async () => {
    if (!selectedFile) return

    setIsUploading(true)
    setError(null)

    try {
      const formData = new FormData()
      formData.append('file', selectedFile)

      const response = await fetch('/api/videos/upload', {
        method: 'POST',
        body: formData,
      })

      if (!response.ok) {
        throw new Error(`Erro no upload: ${response.status}`)
      }

      const result = await response.json()
      onVideoUploaded(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro no upload do vídeo')
    } finally {
      setIsUploading(false)
    }
  }

  const resetUpload = () => {
    setSelectedFile(null)
    setError(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  return (
    <div className="w-full max-w-2xl mx-auto">
      <div
        className={`
          border-2 border-dashed rounded-lg p-8 text-center transition-colors
          ${isDragging ? 'border-blue-400 bg-blue-50' : 'border-gray-300'}
          ${selectedFile ? 'border-green-400 bg-green-50' : ''}
        `}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        {!selectedFile ? (
          <div className="space-y-4">
            <div className="flex justify-center">
              <Upload size={48} className="text-gray-400" />
            </div>
            <div>
              <h3 className="text-lg font-medium text-gray-700 mb-2">
                Faça upload do seu vídeo
              </h3>
              <p className="text-gray-500 mb-4">
                Arraste e solte um arquivo de vídeo aqui ou clique para selecionar
              </p>
              <button
                onClick={() => fileInputRef.current?.click()}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-medium"
              >
                Selecionar Arquivo
              </button>
              <p className="text-xs text-gray-400 mt-2">
                Formatos suportados: MP4, WebM, AVI, MOV (máx. 100MB)
              </p>
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            <div className="flex justify-center">
              <Video size={48} className="text-green-600" />
            </div>
            <div>
              <h3 className="text-lg font-medium text-gray-700">
                Arquivo selecionado
              </h3>
              <p className="text-gray-600 mb-2">{selectedFile.name}</p>
              <p className="text-sm text-gray-500 mb-4">
                Tamanho: {(selectedFile.size / (1024 * 1024)).toFixed(2)} MB
              </p>
              <div className="flex gap-3 justify-center">
                <button
                  onClick={handleUpload}
                  disabled={isUploading}
                  className={`
                    px-6 py-3 rounded-lg font-medium transition-colors
                    ${isUploading 
                      ? 'bg-gray-400 cursor-not-allowed' 
                      : 'bg-green-600 hover:bg-green-700'
                    } text-white
                  `}
                >
                  {isUploading ? 'Enviando...' : 'Processar Vídeo'}
                </button>
                <button
                  onClick={resetUpload}
                  disabled={isUploading}
                  className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-medium disabled:opacity-50"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="video/*"
        onChange={handleFileSelect}
        className="hidden"
      />

      {error && (
        <div className="mt-4 p-4 bg-red-100 border border-red-300 rounded-lg flex items-center gap-2">
          <AlertCircle size={20} className="text-red-600" />
          <span className="text-red-700">{error}</span>
        </div>
      )}

      {isUploading && (
        <div className="mt-4 p-4 bg-blue-100 border border-blue-300 rounded-lg">
          <div className="flex items-center gap-3">
            <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
            <span className="text-blue-700 font-medium">
              Enviando vídeo para processamento...
            </span>
          </div>
        </div>
      )}
    </div>
  )
}
